package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.ActorRef
import org.riskala.model.lobby.LobbyMessages.LobbyInfo
import org.riskala.model.room.RoomMessages.RoomInfo
import argonaut.Argonaut._
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, RoomMessage}

object PlayerMessages {

  trait PlayerMessage

  final case class WrappedMessage(classType: String, payload: String) extends PlayerMessage
  object WrappedMessage {
    implicit def WrappedCodecJson =
      casecodec2(WrappedMessage.apply,WrappedMessage.unapply)("classType","payload")
  }

  final case class SocketMessage(payload: String) extends PlayerMessage

  final case class RegisterSocket(socketActor: actor.ActorRef) extends PlayerMessage

  final case class LobbyReferent(room: ActorRef[LobbyMessage]) extends PlayerMessage

  final case class RoomReferent(room: ActorRef[RoomMessage]) extends PlayerMessage

  final case class GameReferent(room: ActorRef[GameMessage]) extends PlayerMessage

  final case class RoomInfoMessage(roomInfo: RoomInfo) extends PlayerMessage

  final case class LobbyInfoMessage(lobbyInfo: LobbyInfo) extends PlayerMessage

  final case class GameInfoMessage() extends PlayerMessage

  final case class JoinMessage(name: String) extends PlayerMessage
  object JoinMessage {
    implicit def JoinCodecJson =
      casecodec1(JoinMessage.apply,JoinMessage.unapply)("name")
  }

  final case class ReadyMessage() extends PlayerMessage

  final case class CreateMessage(name: String, maxPlayer: Int, scenario: String) extends PlayerMessage
  object CreateMessage {
    implicit def CreateCodecJson =
      casecodec3(CreateMessage.apply,CreateMessage.unapply)("name", "maxPlayer", "scenario")
  }

  final case class LeaveMessage() extends PlayerMessage

  final case class ActionMessage(from: String,
                                 to: String,
                                 attacking: Int,
                                 defending: Int,
                                 invading: Int) extends PlayerMessage
  object ActionMessage {
    implicit def ActionCodecJson =
      casecodec5(ActionMessage.apply,ActionMessage.unapply)("from", "to", "attacking","defending","invading")
  }

  final case class RedeemBonusMessage(cardType: String) extends PlayerMessage
  object RedeemBonusMessage {
    implicit def RedeemBonusCodecJson =
      casecodec1(RedeemBonusMessage.apply,RedeemBonusMessage.unapply)("cardType")
  }

  final case class EndTurnMessage() extends PlayerMessage

  final case class ErrorMessage(error: String) extends PlayerMessage
  object ErrorMessage {
    implicit def ErrorCodecJson =
      casecodec1(ErrorMessage.apply,ErrorMessage.unapply)("error")
  }

}
