package se.marfok.kakurosolver.akka

import se.marfok.kakurosolver.domain.White

sealed trait EntryMessage
case class WhiteUpdate(white: White) extends EntryMessage
case object Reduce extends EntryMessage
