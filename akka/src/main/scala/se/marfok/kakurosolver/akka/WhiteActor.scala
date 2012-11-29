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
    case UpdateWhite(white) => updateWhite(white)
  }
  
  private def subscribe(sender: ActorRef) {
    subscribers = subscribers + sender
  }
  
  private def updateWhite(that: White) {
    white = white.intersect(that)
    subscribers.filterNot(_ == sender).foreach(_ ! WhiteUpdate(white))
    if (white.isCalculated) {
      context.parent ! WhiteSolved(white)
      context.stop(self)
    }
  }
}

object WhiteActor {
  
  def getName(white: White) = "WhiteX" + white.x + "Y" + white.y
}
