package org.riskala.view.messages

import argonaut.Argonaut._

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
object FromClientMessages {

  final case class JoinMessage(name: String)
  object JoinMessage {
    implicit def JoinCodecJson =
      casecodec1(JoinMessage.apply,JoinMessage.unapply)("name")
  }

  final case class ReadyMessage()

  final case class UnReadyMessage()

  final case class CreateMessage(name: String, maxPlayer: Int, scenario: String)
  object CreateMessage {
    implicit def CreateCodecJson =
      casecodec3(CreateMessage.apply,CreateMessage.unapply)("name", "maxPlayer", "scenario")
  }

  final case class LeaveMessage()

  final case class ActionMessage(from: String,
                                 to: String,
                                 attacking: Int,
                                 defending: Int,
                                 invading: Int)
  object ActionMessage {
    implicit def ActionCodecJson =
      casecodec5(ActionMessage.apply,ActionMessage.unapply)("from", "to", "attacking","defending","invading")
  }

  final case class RedeemBonusMessage(cardType: String)
  object RedeemBonusMessage {
    implicit def RedeemBonusCodecJson =
      casecodec1(RedeemBonusMessage.apply,RedeemBonusMessage.unapply)("cardType")
  }

  final case class EndTurnMessage()

  final case class LogoutMessage()
}
