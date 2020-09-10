package org.riskala.controller.actors

import akka.actor
import org.riskala.model.lobby.LobbyMessages.LobbyInfo
import org.riskala.model.room.RoomMessages.RoomInfo
import argonaut.Argonaut._

object PlayerMessages {

  trait PlayerMessage

  final case class WrappedMessage(classType: String, payload: String) extends PlayerMessage
  object WrappedMessage {
    implicit def WrappedCodecJson =
      casecodec2(WrappedMessage.apply,WrappedMessage.unapply)("classType","payload")
  }

  final case class SocketMessage(payload: String) extends PlayerMessage

  final case class RegisterSocket(socketActor: actor.ActorRef) extends PlayerMessage

  final case class RoomInfoMessage(roomInfo: RoomInfo) extends PlayerMessage

  final case class LobbyInfoMessage(lobbyInfo: LobbyInfo) extends PlayerMessage

  final case class GameInfoMessage() extends PlayerMessage

  final case class JoinMessage(name: String) extends PlayerMessage

  final case class CreateMessage(name: String, maxPlayer: Int, scenario: String) extends PlayerMessage

  final case class LeaveMessage() extends PlayerMessage

  final case class DeployMessage(from: String,
                                 to: String,
                                 attacking: Int,
                                 defending: Int,
                                 invading: Int) extends PlayerMessage

  final case class MoveMessage(from: String,
                               to: String,
                               attacking: Int,
                               defending: Int,
                               invading: Int) extends PlayerMessage

  final case class AttackMessage(from: String,
                                 to: String,
                                 attacking: Int,
                                 defending: Int,
                                 invading: Int) extends PlayerMessage

  final case class RedeemBonusMessage(cardType: String) extends PlayerMessage

  final case class EndTurnMessage() extends PlayerMessage

  final case class ErrorMessage(error: String) extends PlayerMessage
  object ErrorMessage {
    implicit def ErrorCodecJson =
      casecodec1(ErrorMessage.apply,ErrorMessage.unapply)("error")
  }

  final case class RoomAlreadyExistsMessage() extends PlayerMessage

  final case class RoomNotFoundMessage() extends PlayerMessage

  final case class GameNotFoundMessage() extends PlayerMessage

}
