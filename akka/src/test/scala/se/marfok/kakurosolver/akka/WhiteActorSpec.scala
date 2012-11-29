package se.marfok.kakurosolver.akka

import org.junit.runner.RunWith
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpec
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.ImplicitSender
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import se.marfok.kakurosolver.domain.White
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WhiteActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpec with MustMatchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("WhiteActorSpec"))

  "WhiteActor" must {
    "give correct name" in {
      val white = White(1, 2, List(1, 2, 3, 4, 5, 6, 7, 8, 9))
      assert(WhiteActor.getName(white) === "WhiteX1Y2")
    }
    "update to new availableNumbers" in {
      val white = White(1, 2, List(1, 2, 3, 4, 5, 6, 7, 8, 9))
      val actorRef = TestActorRef(Props(new WhiteActor(white)))
      val update = White(1, 2, List(1, 2, 3, 4, 5, 6))
      actorRef ! UpdateWhite(update)
      assert(actorRef.underlyingActor.asInstanceOf[WhiteActor].white === update)
    }
    "intersect availableNumbers" in {
      val white = White(1, 2, List(4, 5, 6, 7, 8, 9))
      val actorRef = TestActorRef(Props(new WhiteActor(white)))
      val update = White(1, 2, List(1, 2, 3, 4, 5, 6))
      actorRef ! UpdateWhite(update)
      assert(actorRef.underlyingActor.asInstanceOf[WhiteActor].white === White(1, 2, List(4, 5, 6)))
    }
    "handle subscriptions and unsubscribtions" in {
      val white = White(1, 2, List(1, 2, 3, 4, 5, 6, 7, 8, 9))
      val actorRef = TestActorRef(Props(new WhiteActor(white)))
      val actor = actorRef.underlyingActor.asInstanceOf[WhiteActor]
      assert(actor.subscribers.size === 0)
      actorRef ! Subscribe
      assert(actor.subscribers.size === 1)
    }
  }

  override def afterAll = system.shutdown()
}
