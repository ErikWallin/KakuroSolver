package se.marfok.kakurosolver.domain

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EntrySpec extends Specification {

  "Entry" should {
    "reduce and leave {7, 8, 9} and not be calculated" in {
      val entry = Entry(List(
        White(1, 2, List(1, 2, 3, 4, 5, 6, 7, 8, 9)),
        White(1, 3, List(1, 2, 3, 4, 5, 6, 7, 8, 9)),
        White(1, 4, List(1, 2, 3, 4, 5, 6, 7, 8, 9))),
        24, false).reduce()
      entry.whites.foreach(w => {
        w.availableNumbers must beEqualTo(List(7, 8, 9))
      })
      entry.isCalculated must beFalse
    }
    "reduce and be calculated" in {
      Entry(List(
        White(1, 2, List(1, 2, 3, 4, 9)),
        White(1, 3, List(1, 2, 3, 4, 5, 6, 7, 8, 9))),
        17, false).reduce().isCalculated must beTrue
    }
  }
}
