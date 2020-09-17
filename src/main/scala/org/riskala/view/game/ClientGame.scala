package org.riskala.view.game

import argonaut.Argonaut.{ToJsonIdentity, nospace}
import org.riskala.model.Cards._
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionMessage, RedeemBonusMessage}
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, GameFullInfo, LobbyInfo}
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
    wrappedMsg.classType match {
      case "ErrorMessage" => {
        println("received error message")
        val errorMsg =
          Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        gameFacade.notifyError(errorMsg.error)
      }
      case "GameFullInfo" => {
        println("received game full info ")
        val gameFullInfo =
          Parser.retrieveMessage(wrappedMsg.payload, GameFullInfo.GameFullInfoCodecJson.Decoder).get
        println("Ended parser retrieve message")
        gameFullInfo.players.foreach(pl => gameFacade.addPlayer(pl, gameFullInfo.actualPlayer == pl))
        gameFacade.cleanPlayerState()
        gameFullInfo.playerStates.foreach(ps => gameFacade.addPlayerState(ps))
        gameFacade.loadObjectives(gameFullInfo.personalInfo.objective.info)
        val cardOccurrence = gameFullInfo.personalInfo.cards.groupBy(identity).mapValues(_.size)
        gameFacade.setCardInfo(cardOccurrence.getOrElse(Infantry, 0),
          cardOccurrence.getOrElse(Cavalry, 0),
          cardOccurrence.getOrElse(Artillery, 0))

      }
    }
  }
}
