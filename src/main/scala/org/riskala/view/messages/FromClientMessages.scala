package org.riskala.view.messages

import argonaut.Argonaut._
import org.riskala.model.Cards.Cards

import scala.scalajs.js.annotation.JSExportAll

/**
 * Structure of messages that client sends to server
 * */
@JSExportAll
object FromClientMessages {

  /**
   * @param name Name of the room or game or terminatedGame that the player wants to join
   * */
  final case class JoinMessage(name: String)
  object JoinMessage {
    implicit def JoinCodecJson =
      casecodec1(JoinMessage.apply,JoinMessage.unapply)("name")
  }

  /**
   * @param color color associated to a player
   * */
  final case class ReadyMessage(color: String)
  object ReadyMessage {
    implicit def ReadyMessageCodecJson =
      casecodec1(ReadyMessage.apply,ReadyMessage.unapply)("color")
  }

  final case class UnReadyMessage()

  /**
   * @param name Name of the room that the player creates
   * @param maxPlayer Max number of player into the created room
   * @param scenario Name of the map chosen to play
   * */
  final case class CreateMessage(name: String, maxPlayer: Int, scenario: String)
  object CreateMessage {
    implicit def CreateCodecJson =
      casecodec3(CreateMessage.apply,CreateMessage.unapply)("name", "maxPlayer", "scenario")
  }

  final case class LeaveMessage()

  final case class ActionAttackMessage(from: String,
                                 to: String,
                                 troops: Int)
  object ActionAttackMessage {
    implicit def ActionAttackMessageCodecJson =
      casecodec3(ActionAttackMessage.apply,ActionAttackMessage.unapply)("from", "to", "troops")
  }

  final case class ActionDeployMessage(from: String,
                                 to: String,
                                 troops: Int)
  object ActionDeployMessage {
    implicit def ActionDeployMessageCodecJson =
      casecodec3(ActionDeployMessage.apply,ActionDeployMessage.unapply)("from", "to", "troops")
  }

  final case class ActionMoveMessage(from: String,
                                 to: String,
                                 troops: Int)
  object ActionMoveMessage {
    implicit def ActionMoveMessageCodecJson =
      casecodec3(ActionMoveMessage.apply,ActionMoveMessage.unapply)("from", "to", "troops")
  }

  /**
   * @param card the random card redeemed
   * */
  final case class RedeemBonusMessage(card: Cards)
  object RedeemBonusMessage {
    implicit def RedeemBonusCodecJson =
      casecodec1(RedeemBonusMessage.apply,RedeemBonusMessage.unapply)("cardType")
  }

  final case class EndTurnMessage()

  final case class LogoutMessage()
}
