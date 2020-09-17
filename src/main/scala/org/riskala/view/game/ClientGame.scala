package org.riskala.view.game

import argonaut.Argonaut.{ToJsonIdentity, nospace}
import org.riskala.model.Cards.Cards
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionMessage, RedeemBonusMessage}
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientGame")
object ClientGame {

  @JSExport
  def getEmptyMsgWrapped(typeMessage: String): String =  { //per leaveMsg, logoutMsg e endTurnMsg
    WrappedMessage(typeMessage, "").asJson.pretty(nospace)
  }

  @JSExport
  def getActionMsgWrapped(from: String, to: String, troops: Int): String =  {
    WrappedMessage("ActionMessage",
      ActionMessage(from, to, troops).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  @JSExport
  def getRedeemBonusMsgWrapped(cardType: Cards): String =  {
    WrappedMessage("RedeemBonusMessage", RedeemBonusMessage(cardType).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  @JSExport
  def handleGameMessage(message: String, gameFacade: GameFacade): Unit = {
    println(s"inside handleGame. Message = $message")
    val wrappedMsg = Parser.retrieveWrapped(message).get
    println(s"wrappedMessage = $wrappedMsg")
  }
}
