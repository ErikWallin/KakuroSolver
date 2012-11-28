package se.marfok.kakurosolver.domain

import scala.util.parsing.json.JSON._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Puzzle is the Kakuro puzzle and holds all entries and whites.
 */
case class Puzzle(whites: List[White], entries: List[Entry]) {

  def isCalculated = !whites.exists(!_.isCalculated)

  def toSquares = {
    val maxX = whites.sortBy(-_.x).head.x
    val maxY = whites.sortBy(-_.y).head.y
    (for {
      x <- 0 to maxX
    } yield {
      (for {
        y <- 0 to maxY
      } yield {
        val white = whites.find(w => w.x == x && w.y == y)
        val isWhite = white.isDefined
        val solution = if (isWhite && white.get.availableNumbers.size == 1) Some(white.get.availableNumbers.head) else None
        val horizontalEntrySum = if (x < maxX) {
          val entry = entries.filter(_.isHorizontal).find(e => {
            val w = e.whites.sortBy(_.x).head
            w.x == x + 1 && w.y == y
          })
          if (entry.isDefined) Some(entry.get.sum) else None
        } else None
        val verticalEntrySum = if (y < maxY) {
          val entry = entries.filter(!_.isHorizontal).find(e => {
            val w = e.whites.sortBy(_.y).head
            w.x == x && w.y == y + 1
          })
          if (entry.isDefined) Some(entry.get.sum) else None
        } else None
        Square(isWhite, solution, horizontalEntrySum, verticalEntrySum)
      }).toList
    }).toList.transpose
  }

  def toCsv =
    toSquares
      .map(lb => lb.map(b => b.toCsv).reduceLeft((s, b) => s + "|" + b))
      .reduceLeft((a, b) => a + "\n" + b)

  def toJson =
    "{\"rows\": [" +
      toSquares
      .map(lb => "{\"row\": [" + lb.map(b => b.toJson).reduceLeft((s, b) => s + ", " + b) + "]}")
      .reduceLeft((a, b) => a + ", " + b) +
      "]}"
}

object Puzzle {

  /**
   * Assume rectangular shape of puzzle. The form is illustrated with following example.
   *
   * {"rows": [{"row": [{"isWhite": false},
   *                    {"isWhite": false, "vertical": 17},
   *                    {"isWhite": false, "vertical": 3}]},
   *           {"row": [{"isWhite": false, "horizontal": 12},
   *                    {"isWhite": true},
   *                    {"isWhite": true}]},
   *           {"row": [{"isWhite": false, "horizontal": 8},
   *                    {"isWhite": true},
   *                    {"isWhite": false}]}]}
   */
  def fromJson(json: String): Puzzle = {
    def bail = throw new IllegalArgumentException("Not a valid json format: " + json)
    val rows = parseFull(json).getOrElse(bail).asInstanceOf[Map[String, Any]]
      .get("rows").getOrElse(bail).asInstanceOf[List[Any]]

    val squares = rows
      .map(r => r.asInstanceOf[Map[String, Any]]
        .get("row").getOrElse(bail).asInstanceOf[List[Any]]
        .map(b => b.asInstanceOf[Map[String, Any]])
        .map(Square(_)))
    val squaresArray = squares.map(_.toArray).toArray.transpose
    val whites = createwhites(squaresArray)
    val entries = createHorizontalEntrys(squaresArray, whites) ::: createVerticalEntrys(squaresArray, whites)
    Puzzle(whites, entries)
  }

  /**
   * Assume rectangular shape of puzzle
   * Rows are split with new line
   * whites are split with |
   * "     " = White
   * "12\  " = Vertical Entry with sum 12
   * "  \34" = Horizontal Entry with sum 34
   * " 5\12" = Two entries
   */
  def fromCsv(csv: String): Puzzle = {
    val parts = csv.split('\n').map(_.split('|').map(Square(_))).transpose
    val whites = createwhites(parts)
    val entries = createHorizontalEntrys(parts, whites) ::: createVerticalEntrys(parts, whites)
    Puzzle(whites, entries)
  }

  private def createwhites(squares: Array[Array[Square]]): List[White] = {
    val whites = (for {
      x <- 0 until squares.length
      y <- 0 until squares(x).length
      if squares(x)(y).isWhite
    } yield White(x, y, (1 to 9).toList)).toList
    whites
  }

  private def createHorizontalEntrys(squares: Array[Array[Square]], allwhites: List[White]): List[Entry] = {
    (for {
      xi <- 0 until squares.length
      yi <- 0 until squares(xi).length
      if !squares(xi)(yi).isWhite
      sum = squares(xi)(yi).horizontalEntrySum
      if sum != None
    } yield {
      val startX = xi + 1
      var endX = xi + 1
      while (endX < squares.length && squares(endX)(yi).isWhite) endX = endX + 1
      val whites = (for (x <- startX until endX) yield allwhites.find(w => w.x == x && w.y == yi).get).toList
      Entry(whites, sum.get, true)
    }).toList
  }

  private def createVerticalEntrys(squares: Array[Array[Square]], allwhites: List[White]): List[Entry] = {
    (for {
      xi <- 0 until squares.length
      yi <- 0 until squares(0).length
      if !squares(xi)(yi).isWhite
      sum = squares(xi)(yi).verticalEntrySum
      if sum != None
    } yield {
      val startY = yi + 1
      var endY = yi + 1
      while (endY < squares.length && squares(xi)(endY).isWhite) endY = endY + 1
      val whites = (for (y <- startY until endY) yield allwhites.find(w => w.x == xi && w.y == y).get).toList
      Entry(whites, sum.get, false)
    }).toList
  }
}
