package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.ActorRef
import org.riskala.model.{MapGeography, Player, PlayerState}
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

  final case class GameInfoMessage(players: Set[String],
                                   actualPlayer: String,
                                   troopsToDeploy: Int,
                                   map: MapGeography,
                                   isDeployOnly: Boolean,
                                   playerStates: Set[PlayerState],
                                   personalInfo: GamePersonalInfo,
                                   winner: Option[Player]) extends PlayerMessage

  final case class GameUpdateMessage(actualPlayer: String,
                                     troopsToDeploy: Int,
                                     isDeployOnly: Boolean,
                                     playerStates: Set[PlayerState],
                                     personalInfo: GamePersonalInfo) extends PlayerMessage

  final case class GameEndMessage(winner: Player) extends PlayerMessage

  final case class ErrorMessage(error: String) extends PlayerMessage

}
