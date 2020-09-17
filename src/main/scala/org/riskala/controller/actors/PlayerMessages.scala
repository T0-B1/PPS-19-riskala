package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.ActorRef
import org.riskala.model.{MapGeography, PlayerState}
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, RoomMessage}
import org.riskala.view.messages.ToClientMessages.{GamePersonalInfo, LobbyInfo, RoomInfo}

object PlayerMessages {

  trait PlayerMessage

  final case class SocketMessage(payload: String) extends PlayerMessage

  final case class RegisterSocket(socketActor: actor.ActorRef) extends PlayerMessage

  final case class LobbyReferent(room: ActorRef[LobbyMessage]) extends PlayerMessage

  final case class RoomReferent(room: ActorRef[RoomMessage]) extends PlayerMessage

  final case class GameReferent(game: ActorRef[GameMessage]) extends PlayerMessage

  final case class RoomInfoMessage(roomInfo: RoomInfo) extends PlayerMessage

  final case class LobbyInfoMessage(lobbyInfo: LobbyInfo) extends PlayerMessage

  final case class GameInfoMessage(players:Set[String],
                                   actualPlayer:String,
                                   map:MapGeography,
                                   playerStates: Set[PlayerState],
                                   personalInfo:GamePersonalInfo) extends PlayerMessage

  final case class ErrorMessage(error: String) extends PlayerMessage

}
