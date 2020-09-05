package org.riskala.controller.actors

object ServerMessages {

  trait ServerMessage

  trait PlayerMessage extends ServerMessage

  final case class SocketMessage(payload: String) extends PlayerMessage

}
