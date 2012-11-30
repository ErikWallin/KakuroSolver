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
    val oldEntry = entry
    entry = entry.whiteUpdate(white)
    if (oldEntry != entry) {
      if (entry.isCalculated) context.parent ! EntrySolved(entry)
      else self ! Reduce
    }
  }

  private def reduce: Unit = {
    val oldEntry = entry
    entry = entry.reduce
    if (oldEntry != entry) {
      val changedwhites = entry.whites.filter(w => w.availableNumbers.size < oldEntry.whites.find(os => os.x == w.x && os.y == w.y).get.availableNumbers.size)
      changedwhites.foreach(w => context.actorFor("../" + WhiteActor.getName(w)) ! UpdateWhite(w))
      if (entry.isCalculated) {
        context.parent ! EntrySolved(entry)
      }
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
