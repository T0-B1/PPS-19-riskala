package org.riskala.view.messages

import argonaut.Argonaut._
import org.riskala.modelToFix.Cards.Cards

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
object FromClientMessages {

  final case class JoinMessage(name: String)
  object JoinMessage {
    implicit def JoinCodecJson =
      casecodec1(JoinMessage.apply,JoinMessage.unapply)("name")
  }

  final case class ReadyMessage(color: String)
  object ReadyMessage {
    implicit def ReadyMessageCodecJson =
      casecodec1(ReadyMessage.apply,ReadyMessage.unapply)("color")
  }

  final case class UnReadyMessage()

  final case class CreateMessage(name: String, maxPlayer: Int, scenario: String)
  object CreateMessage {
    implicit def CreateCodecJson =
      casecodec3(CreateMessage.apply,CreateMessage.unapply)("name", "maxPlayer", "scenario")
  }

  final case class LeaveMessage()

  final case class ActionMessage(from: String,
                                 to: String,
                                 troops: Int)
  object ActionMessage {
    implicit def ActionCodecJson =
      casecodec3(ActionMessage.apply,ActionMessage.unapply)("from", "to", "troops")
  }

  final case class RedeemBonusMessage(card: Cards)
  object RedeemBonusMessage {
    implicit def RedeemBonusCodecJson =
      casecodec1(RedeemBonusMessage.apply,RedeemBonusMessage.unapply)("cardType")
  }

  final case class EndTurnMessage()

  final case class LogoutMessage()
}
