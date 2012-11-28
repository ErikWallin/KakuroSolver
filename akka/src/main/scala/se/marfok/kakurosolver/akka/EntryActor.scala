package se.marfok.kakurosolver.akka

import akka.actor.Actor
import akka.actor.ActorLogging
import se.marfok.kakurosolver.domain.Entry
import se.marfok.kakurosolver.domain.White
import akka.event.LoggingReceive

/**
 * Actor for an entry, one actor per entry. 
 */
class EntryActor(var entry: Entry) extends Actor with ActorLogging {

  override def preStart() = {
    entry.whites.foreach(w => context.actorFor("../" + WhiteActor.getName(w)) ! Subscribe)
  }

  def receive = LoggingReceive {
    case WhiteUpdate(white) => whiteUpdate(white)
    case Reduce => reduce
  }

  private def whiteUpdate(white: White): Unit = {
    entry = entry.whiteUpdate(white)
    if (entry.isCalculated) context.parent ! EntrySolved(entry)
    else self ! Reduce
  }

  private def reduce: Unit = {
    val oldwhites = entry.whites
    entry = entry.reduce
    val changedwhites = entry.whites.filter(w => w.availableNumbers.size < oldwhites.find(os => os.x == w.x && os.y == w.y).get.availableNumbers.size)
    changedwhites.foreach(w => context.actorFor("../" + WhiteActor.getName(w)) ! UpdateWhite(w))
    entry.whites.filter(_.isCalculated).foreach(s => context.actorFor("../" + WhiteActor.getName(s)) ! Unsubscribe)
    if (entry.isCalculated) {
      context.parent ! EntrySolved(entry)
      context.stop(self)
    }
  }
}

object EntryActor {
  def getName(entry: Entry) = {
    val sortedWhites = entry.whites.sortBy(w => w.x + w.y)
    val minWhite = sortedWhites.head
    val maxWhite = sortedWhites.last
    "EntryX" + minWhite.x + "Y" + minWhite.y + "X" + maxWhite.x + "Y" + maxWhite.y
  }
}
