package se.marfok.kakurosolver.akka

import se.marfok.kakurosolver.domain.Puzzle
import se.marfok.kakurosolver.domain.White
import org.junit.runner.RunWith
import akka.testkit.TestActorRef
import akka.actor.Props
import org.junit.runner.RunWith
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import se.marfok.kakurosolver.domain.White
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import akka.testkit.ImplicitSender
import akka.actor.Props

@RunWith(classOf[JUnitRunner])
class PuzzleActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpec with MustMatchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("PuzzleActorSpec"))

  "PuzzleActor" should {
    "solve and return" in {
      val json =
        "{\"rows\": [" +
          "{\"row\": [{\"isWhite\": false}, {\"isWhite\": false, \"vertical\": 17}, {\"isWhite\": false, \"vertical\": 3}]}, " +
          "{\"row\": [{\"isWhite\": false, \"horizontal\": 12}, {\"isWhite\": true}, {\"isWhite\": true}]}, " +
          "{\"row\": [{\"isWhite\": false, \"horizontal\": 8}, {\"isWhite\": true}, {\"isWhite\": false}]}" +
          "]}"
      val puzzle = Puzzle.fromJson(json)
      val actorRef = TestActorRef(Props(new PuzzleActor(puzzle)))
      actorRef ! Solve
      expectMsgType[Puzzle]
    }
  }
}