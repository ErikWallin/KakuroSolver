package se.marfok.kakurosolver.akka

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.Timeout.intToTimeout
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import se.marfok.kakurosolver.domain.Puzzle
import se.marfok.kakurosolver.domain.White
import se.marfok.kakurosolver.domain.Entry
import scala.io.Source

object KakuroAkkaMain {

  def main(args: Array[String]): Unit = {

    val puzzle = getPuzzle(args)
    println(puzzle.toCsv)

    val config = ConfigFactory.load()
    val system = ActorSystem("KakuroSolver", config)
    val puzzleActorRef = system.actorOf(Props(new PuzzleActor(puzzle)))
    
    solve(puzzleActorRef)

    system.shutdown()
  }

  private def getPuzzle(args: Array[String]): Puzzle =
    if (args.length < 1) getCsvPuzzle
    else {
      val fileName = args(0)
      val content = Source.fromFile(fileName).mkString
      if (content.contains('|')) Puzzle.fromCsv(content)
      else Puzzle.fromJson(content)
    }

  private def getCsvPuzzle: Puzzle = {
    val csv =
      """  \  |  \  | 9\  | 7\  |  \  |16\  | 9\  |  \  |27\  |14\  |  \  | 6\  |14\  |11\  
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
    Puzzle.fromCsv(csv)
  }

  private def getJsonPuzzleSmall: Puzzle = {
    val json =
      "{\"rows\": [" +
        "{\"row\": [{\"isWhite\": false}, {\"isWhite\": false, \"vertical\": 17}, {\"isWhite\": false, \"vertical\": 3}]}, " +
        "{\"row\": [{\"isWhite\": false, \"horizontal\": 12}, {\"isWhite\": true}, {\"isWhite\": true}]}, " +
        "{\"row\": [{\"isWhite\": false, \"horizontal\": 8}, {\"isWhite\": true}, {\"isWhite\": false}]}" +
        "]}"
    Puzzle.fromJson(json)
  }

  private def solve(puzzleActorRef: akka.actor.ActorRef): Unit = {
    implicit val timeout = Timeout(60 seconds)
    val start = System.currentTimeMillis
    val future = puzzleActorRef.ask(Solve)
    val result = Await.result(future, timeout.duration).asInstanceOf[Puzzle]
    val diff = System.currentTimeMillis - start
    println("Solution time: " + diff + " ms")
    println(result.toCsv)
  }
}