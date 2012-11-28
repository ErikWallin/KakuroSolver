package se.marfok.kakurosolver.domain

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SquareSpec extends Specification {

  "Square csv apply method" should {
    "create white, i.e. Square(true, None, None, None) for '     '" in {
      Square("     ") must beEqualTo(Square(true, None, None, None))
    }
    "create solved white, i.e. Square(true, Some(7), None, None) for '  7  '" in {
      Square("  7  ") must beEqualTo(Square(true, Some(7), None, None))
    }
    "create black, i.e. Square(false, None, None, None) for '  \\  '" in {
      Square("  \\  ") must beEqualTo(Square(false, None, None, None))
    }
    "create vertical entry, i.e. Square(false, None, None, Some(9)) for ' 9\\  '" in {
      Square(" 9\\  ") must beEqualTo(Square(false, None, None, Some(9)))
    }
    "create horizontal entry, i.e. Square(false, None, Some(23), None) for '  \\23'" in {
      Square("  \\23") must beEqualTo(Square(false, None, Some(23), None))
    }
    "create vertical and horizontal entry, i.e. Square(false, None, Some(6), Some(25)) for '25\\6 '" in {
      Square("25\\6 ") must beEqualTo(Square(false, None, Some(6), Some(25)))
    }
  }

  "Square json apply method" should {
    "create white, i.e. Square(true, None, None, None) for Map(\"isWhite\" -> true)" in {
      val json: Map[String, Any] = Map("isWhite" -> true)
      Square(json) must beEqualTo(Square(true, None, None, None))
    }
    "create solved white, i.e. Square(true, Some(7), None, None) for Map(\"isWhite\" -> true, \"solution\" -> 7)" in {
      val json: Map[String, Any] = Map("isWhite" -> true, "solution" -> 7)
      Square(json) must beEqualTo(Square(true, Some(7), None, None))
    }
    "create black, i.e. Square(false, None, None, None) for Map(\"isWhite\" -> false)" in {
      val json: Map[String, Any] = Map("isWhite" -> false)
      Square(json) must beEqualTo(Square(false, None, None, None))
    }
    "create vertical entry, i.e. Square(false, None, None, Some(9)) for Map(\"isWhite\" -> false, \"vertical\" -> 9)" in {
      val json: Map[String, Any] = Map("isWhite" -> false, "vertical" -> 9)
      Square(json) must beEqualTo(Square(false, None, None, Some(9)))
    }
    "create horizontal entry, i.e. Square(false, None, Some(23), None) for Map(\"isWhite\" -> false, \"horizontal\" -> 23)" in {
      val json: Map[String, Any] = Map("isWhite" -> false, "horizontal" -> 23)
      Square(json) must beEqualTo(Square(false, None, Some(23), None))
    }
    "create vertical and horizontal entry, i.e. None, Square(false, Some(6), Some(25)) for Map(\"isWhite\" -> false, \"horizontal\" -> 6, \"vertical\" -> 25)" in {
      val json: Map[String, Any] = Map("isWhite" -> false, "horizontal" -> 6, "vertical" -> 25)
      Square(json) must beEqualTo(Square(false, None, Some(6), Some(25)))
    }
  }
}
