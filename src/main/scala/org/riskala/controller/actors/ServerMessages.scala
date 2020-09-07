package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.ActorRef
import akka.http.scaladsl.model.ws.Message

object ServerMessages {

  trait ServerMessage

  trait PlayerMessage extends ServerMessage

  final case class SocketMessage(payload: String) extends PlayerMessage

  final case class RegisterSocket(socketActor: actor.ActorRef) extends PlayerMessage

}
