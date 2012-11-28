package se.marfok.kakurosolver.domain

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PuzzleSpec extends Specification {

  "Puzzle" should {
    val csv = """  \  |  \  | 9\  | 7\  |  \  |16\  | 9\  |  \  |27\  |14\  |  \  | 6\  |14\  |11\  
  \  | 3\4 |     |     |19\3 |     |     |  \4 |     |     | 6\10|     |     |     
  \27|     |     |     |     |     |     |21\22|     |     |     |     |     |     
  \5 |     |     |  \29|     |     |     |     |     |     |     |16\12|     |     
  \  | 3\  |14\  |  \3 |     |     |41\6 |     |     |     |23\9 |     |     |     
  \3 |     |     |37\  |  \13|     |     |     |     | 8\13|     |     |38\  |  \  
  \16|     |     |     |  \  |36\41|     |     |     |     |     |     |     |  \  
  \  |  \4 |     |     | 6\23|     |     |     |20\17|     |     |     |     |19\  
  \  | 3\  |19\39|     |     |     |     |     |     | 5\  |  \6 |     |     |     
  \21|     |     |     |     |     |     | 3\13|     |     |  \  |17\12|     |     
  \6 |     |     |     | 6\17|     |     |     |     |     | 7\7 |     |     |     
  \  | 5\30|     |     |     |     |     |     |     |14\24|     |     |     |     
  \19|     |     |     |     |     |  \  |  \  |  \15|     |     |     |     |  \  
  \3 |     |     |  \6 |     |     |  \  |  \  |  \20|     |     |     |  \  |  \  """

    "read from csv string" in {
      val puzzle = Puzzle.fromCsv(csv)
      puzzle.whites.size must beEqualTo(124)
      puzzle.entries.size must beEqualTo(69)
      puzzle.entries.filter(_.sum == 17).size must beEqualTo(3)
      puzzle.entries.find(_.sum == 36).get.whites.size must beEqualTo(7)
      puzzle.entries.filter(_.sum == 12).head.whites.size must beEqualTo(2)
    }
    "write to csv string" in {
      val puzzle = Puzzle.fromCsv(csv)
      val result = puzzle.toCsv
      csv must beEqualTo(result)
    }
  }

  "Puzzle" should {
    val json =
      "{\"rows\": [" +
        "{\"row\": [{\"isWhite\": false}, {\"isWhite\": false, \"vertical\": 17}, {\"isWhite\": false, \"vertical\": 3}]}, " +
        "{\"row\": [{\"isWhite\": false, \"horizontal\": 12}, {\"isWhite\": true}, {\"isWhite\": true}]}, " +
        "{\"row\": [{\"isWhite\": false, \"horizontal\": 8}, {\"isWhite\": true}, {\"isWhite\": false}]}" +
        "]}"

    "read from json string" in {
      val puzzle = Puzzle.fromJson(json)
      puzzle.whites.size must beEqualTo(3)
      puzzle.entries.size must beEqualTo(4)
      puzzle.entries.filter(_.sum == 17).size must beEqualTo(1)
      puzzle.entries.find(_.sum == 12).get.whites.size must beEqualTo(2)
      puzzle.entries.filter(_.sum == 8).head.whites.size must beEqualTo(1)
    }
    "write to json string" in {
      val puzzle = Puzzle.fromJson(json)
      val result = puzzle.toJson
      json must beEqualTo(result)
    }
  }
}
