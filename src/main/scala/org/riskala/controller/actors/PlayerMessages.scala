package org.riskala.controller.actors

object PlayerMessages {

  trait PlayerMessage

  final case class SocketMessage(payload: String) extends PlayerMessage

}
