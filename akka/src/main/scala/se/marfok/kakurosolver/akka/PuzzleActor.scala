package se.marfok.kakurosolver.akka

import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.LoggingReceive
import se.marfok.kakurosolver.domain.Puzzle
import se.marfok.kakurosolver.domain.White
import se.marfok.kakurosolver.domain.Entry

class PuzzleActor(var puzzle: Puzzle) extends Actor with ActorLogging {

  var whites: Set[White] = Set()
  var entries: Set[Entry] = Set()
  var children: Set[ActorRef] = Set()
  var starter: ActorRef = _

  def receive = LoggingReceive {
    case Solve => solve(sender)
    case WhiteSolved(white) => whitesolved(sender, white)
    case EntrySolved(entry) => entrySolved(sender, entry)
  }

  def solve(starter: ActorRef): Unit = {
    this.starter = starter
    val whites = puzzle.whites.map(w => context.actorOf(Props(new WhiteActor(w)), WhiteActor.getName(w)))
    val entries = puzzle.entries.map(e => context.actorOf(Props(new EntryActor(e)).withDispatcher("akka.actor.entry-dispatcher"), EntryActor.getName(e)))
    children = entries.toSet ++ whites.toSet
    entries.foreach(_ ! Reduce)
  }

  def whitesolved(child: ActorRef, white: White): Unit = {
    whites = whites + white
    removeChild(child)
  }

  def entrySolved(child: ActorRef, entry: Entry): Unit = {
    entries = entries + entry
    removeChild(child)
  }

  private def removeChild(child: akka.actor.ActorRef): Unit = {
    children = children.filterNot(_ == child)
    if (children.size <= 0) {
      starter ! Puzzle(whites.toList, entries.toList)
      context.stop(self)
    }
  }
}
