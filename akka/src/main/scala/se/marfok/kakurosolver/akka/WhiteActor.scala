package se.marfok.kakurosolver.akka

import akka.actor.Actor
import akka.actor.ActorLogging
import se.marfok.kakurosolver.domain.White
import akka.actor.ActorRef
import akka.event.LoggingReceive

class WhiteActor(var white: White) extends Actor with ActorLogging {

  var subscribers: Set[ActorRef] = Set()

  def receive = LoggingReceive {
    case Subscribe => subscribe(sender)
    case UpdateWhite(w) => updateWhite(w)
  }

  private def subscribe(sender: ActorRef): Unit = {
    subscribers = subscribers + sender
  }

  private def updateWhite(that: White): Unit = {
    val oldWhite = white
    white = white.intersect(that)
    if (oldWhite != white) {
      subscribers.filterNot(_ == sender).foreach(_ ! WhiteUpdate(white))
      if (white.isCalculated) {
        context.parent ! WhiteSolved(white)
      }
    }
  }
}

object WhiteActor {

  def getName(white: White) = "WhiteX" + white.x + "Y" + white.y
}
