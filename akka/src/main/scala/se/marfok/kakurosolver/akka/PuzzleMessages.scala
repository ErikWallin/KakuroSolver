package se.marfok.kakurosolver.akka

import se.marfok.kakurosolver.domain.White
import se.marfok.kakurosolver.domain.Entry

sealed trait PuzzleMessage
case object Solve extends PuzzleMessage
case class WhiteSolved(white: White) extends PuzzleMessage
case class EntrySolved(entry: Entry) extends PuzzleMessage
