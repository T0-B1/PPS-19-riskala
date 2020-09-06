package org.riskala.controller.actors

import akka.actor.typed.ActorRef
import akka.http.scaladsl.model.ws.Message
import org.riskala.model.lobby.LobbyMessages.LobbyInfo
import org.riskala.model.room.RoomMessages.RoomInfo

object PlayerMessages {

  trait PlayerMessage

  final case class SocketMessage(payload: String) extends PlayerMessage

  final case class RegisterSocket(socketActor: ActorRef[Message]) extends PlayerMessage

  final case class RoomInfoMessage(roomInfo: RoomInfo) extends PlayerMessage

  final case class LobbyInfoMessage(lobbyInfo: LobbyInfo) extends PlayerMessage

  final case class GameInfoMessage() extends PlayerMessage

  final case class RoomAlreadyExistsMessage() extends PlayerMessage

  final case class RoomNotFoundMessage() extends PlayerMessage

  final case class GameNotFoundMessage() extends PlayerMessage

}
