package se.marfok.kakurosolver.domain

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WhiteSpec extends Specification {

  "White" should {
    val whiteX1Y1L1 = White(1, 2, List(1))
    val whiteX1Y1L123 = White(1, 1, List(1, 2, 3))
    val whiteX1Y2L123 = White(1, 2, List(1, 2, 3))
    val whiteX1Y2L2345 = White(1, 2, List(2, 3, 4, 5))
    val whiteX1Y3L123 = White(1, 3, List(1, 2, 3))
    val whites = List(whiteX1Y1L123, whiteX1Y2L123, whiteX1Y2L2345, whiteX1Y3L123)
    "be calculated " in {
      whiteX1Y1L1.isCalculated must beTrue
    }
    "not be calculated if it contains more than one number" in {
      (whiteX1Y1L123.isCalculated) must beFalse
    }
    "merge whites on same position" in {
      val result = White.intersect(whites)
      result.size must beEqualTo(3)
    }
    "merge availableNumbers" in {
      val result = White.intersect(List(whiteX1Y2L123, whiteX1Y2L2345)).head.availableNumbers
      result.contains(1) must beFalse
      result.contains(2) must beTrue
      result.contains(3) must beTrue
      result.contains(4) must beFalse
      result.contains(5) must beFalse
    }
  }
}
