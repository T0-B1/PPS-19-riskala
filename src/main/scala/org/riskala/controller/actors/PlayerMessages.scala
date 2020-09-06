package org.riskala.controller.actors

import akka.actor.typed.ActorRef
import akka.http.scaladsl.model.ws.Message

object PlayerMessages {

  trait PlayerMessage

  final case class SocketMessage(payload: String) extends PlayerMessage

  final case class RegisterSocket(socketActor: ActorRef[Message]) extends PlayerMessage

}
