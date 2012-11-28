package se.marfok.kakurosolver.domain

/**
 * An Entry holds a certain amount of whites that should sum to a defined value. It is like a word in a crossword.
 */
case class Entry(whites: List[White], sum: Int, isHorizontal: Boolean) {

  def isCalculated = !whites.exists(!_.isCalculated)

  def reduce(): Entry = {
    def reduce0(solution: List[White], whites0: List[White]): List[List[White]] = {
      whites0 match {
        case List() => if (solution.foldRight(0)((a, b) => a.availableNumbers.head + b) == sum) List(solution) else List()
        case w :: ws => w.availableNumbers.filter(
          n => !solution.exists(
            w => w.availableNumbers.contains(n))).map(
            n => reduce0(White(w.x, w.y, List(n)) :: solution, ws)).flatten
      }
    }
    Entry(
      reduce0(List(), whites)
        .transpose.map(_.distinct)
        .map(w => White(w.head.x, w.head.y, w.foldLeft(List[Int]())((a, b) => b.availableNumbers.head :: a).sortWith(_ < _)))
        .sortBy(w => w.x + w.y), sum, isHorizontal)
  }

  def whiteUpdate(white: White): Entry = {
    Entry(white :: whites.filterNot(w => w.x == white.x && w.y == white.y), sum, isHorizontal)
  }
}