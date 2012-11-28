package se.marfok.kakurosolver.akka

import se.marfok.kakurosolver.domain.Entry
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
class EntryActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpec with MustMatchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("EntryActorSpec"))
  
  "EntryActor" should {
    "give correct name" in {
      val entry = getEntry
      assert(EntryActor.getName(entry) === "EntryX1Y2X1Y4")
    }
    "reduce to 7,8,9 in" in {
      val entry = getEntry
      val actorRef = TestActorRef(Props(new EntryActor(entry)))
      val actor = actorRef.underlyingActor.asInstanceOf[EntryActor]
      actorRef ! Reduce
      val expectedReduction = Entry(List(
        White(1, 2, List(7, 8, 9)),
        White(1, 3, List(7, 8, 9)),
        White(1, 4, List(7, 8, 9))),
        24, false)
      assert(actorRef.underlyingActor.asInstanceOf[EntryActor].entry === expectedReduction)
    }
    "update and reduce to 7, [89], [89] in" in {
      val entry = getEntry
      val actorRef = TestActorRef(Props(new EntryActor(entry)))
      val actor = actorRef.underlyingActor.asInstanceOf[EntryActor]
      actorRef ! WhiteUpdate(White(1, 2, List(7)))
      val expectedReduction = Entry(List(
        White(1, 2, List(7)),
        White(1, 3, List(8, 9)),
        White(1, 4, List(8, 9))),
        24, false)
      assert(actorRef.underlyingActor.asInstanceOf[EntryActor].entry === expectedReduction)
    }
  }

  private def getEntry =
    Entry(List(
      White(1, 2, List(1, 2, 3, 4, 5, 6, 7, 8, 9)),
      White(1, 3, List(1, 2, 3, 4, 5, 6, 7, 8, 9)),
      White(1, 4, List(1, 2, 3, 4, 5, 6, 7, 8, 9))),
      24, false)
}