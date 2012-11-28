package se.marfok.kakurosolver.domain

/**
 * White is the place in the puzzle where a number should be filled in.
 */
case class White(x: Int, y: Int, availableNumbers: List[Int]) {

  def isCalculated = availableNumbers.size == 1

  def intersect(that: White): White = {
    require(x == that.x && y == that.y)
    White(x, y, availableNumbers.intersect(that.availableNumbers).sortWith(_ < _))
  }
}

object White {

  def intersect(whites: List[White]): List[White] = {
    whites
      .groupBy(w => (w.x, w.y))
      .map { case (xy, ss) => ss.reduceLeft(_.intersect(_)) }
      .toList
  }
}