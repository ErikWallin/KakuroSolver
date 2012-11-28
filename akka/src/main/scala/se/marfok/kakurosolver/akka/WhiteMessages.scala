package se.marfok.kakurosolver.akka

import se.marfok.kakurosolver.domain.White

sealed trait WhiteMessage
case class UpdateWhite(white: White) extends WhiteMessage
case object Subscribe extends WhiteMessage
case object Unsubscribe extends WhiteMessage

