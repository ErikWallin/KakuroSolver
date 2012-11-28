package se.marfok.kakurosolver.domain

import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Square is a class that represents different fields that each square in a kakuro puzzle can hold.
 * It is used when parsing to/from json/csv format.
 */
case class Square(isWhite: Boolean, solution: Option[Int], horizontalEntrySum: Option[Int], verticalEntrySum: Option[Int]) {

  def toJson = "{\"isWhite\": " + isWhite +
    (if (horizontalEntrySum.isEmpty) "" else (", \"horizontal\": " + horizontalEntrySum.get)) +
    (if (verticalEntrySum.isEmpty) "" else (", \"vertical\": " + verticalEntrySum.get)) +
    "}"

  def toCsv: String = {
    val v = verticalEntrySum match {
      case Some(s) if (s < 10) => " " + s
      case Some(s) => s.toString
      case None => "  "
    }
    val s = (isWhite, solution) match {
      case (false, _) => "\\"
      case (true, None) => " "
      case (true, Some(s)) => s.toString
    }
    val h = horizontalEntrySum match {
      case Some(s) if (s < 10) => s + " "
      case Some(s) => s.toString
      case None => "  "
    }
    v + s + h
  }
}

object Square {

  def apply(csvPart: String): Square = {
    val twoEntryPattern = """([ 0-9][ 0-9])(.)([ 0-9][ 0-9])""".r
    csvPart match {
      case twoEntryPattern(vSum, separator, hSum) => {
        val (isWhite, solution) = separator match {
          case " " => (true, None)
          case "\\" => (false, None)
          case s => (true, Some(s.toInt))
        }
        val horizontalEntrySum = if (hSum.trim.isEmpty) None else Some(hSum.trim.toInt)
        val verticalEntrySum = if (vSum.trim.isEmpty) None else Some(vSum.trim.toInt)
        Square(isWhite, solution, horizontalEntrySum, verticalEntrySum)
      }
      case _ => throw new IllegalArgumentException(csvPart)
    }
  }

  def apply(part: Map[String, Any]): Square =
    Square(part.get("isWhite").getOrElse(throw new IllegalArgumentException(part.toString)).asInstanceOf[Boolean], for { s <- part.get("solution") } yield s.toString.toDouble.toInt, for { h <- part.get("horizontal") } yield h.toString.toDouble.toInt, for { v <- part.get("vertical") } yield v.toString.toDouble.toInt)

}