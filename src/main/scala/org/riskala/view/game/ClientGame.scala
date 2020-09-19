package org.riskala.view.game

import argonaut.Argonaut.{ToJsonIdentity, nospace}
import org.riskala.model.Cards._
import org.riskala.model.{Cards, MapGeography, PlayerState}
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionMessage, RedeemBonusMessage}
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, GameFullInfo, GameUpdate, LobbyInfo}
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientGame")
object ClientGame {

  private var map: MapGeography = _
  private var playerStates: Set[PlayerState] = _

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
  def getRedeemBonusMsgWrapped(cardType: String): String =  {
    val card = Cards.withName(cardType)
    WrappedMessage("RedeemBonusMessage", RedeemBonusMessage(card).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  @JSExport
  def neighborClick(clickedState: String, namePlayer: String, mapSelectedState: String, gameFacade: GameFacade): Unit = {
    if(clickedState equals mapSelectedState) {
      gameFacade.nameActionBtn = "Deploy"
      gameFacade.maxAvailableTroops = gameFacade.troopsToDeploy
    } else{
      playerStates.find(_.state == clickedState)
        .foreach(ps => {
          gameFacade.nameActionBtn = if (ps.owner.nickname == namePlayer) "Move" else "Attack"
          gameFacade.maxAvailableTroops = playerStates.find(_.state == mapSelectedState).get.troops - 1
        }
      )
    }
  }

  private def setStateInfo(playerState:PlayerState, gameFacade: GameFacade, myState: Boolean): Unit = {
    println("setStateInfo")
    gameFacade.setStateInfo(playerState.state,
      playerState.owner.nickname,
      playerState.troops,
      map.regions.find(_.states.contains(playerState.state)).map(_.name).getOrElse(""))

    if(myState){
      gameFacade.visible = true
      if(gameFacade.troopsToDeploy > 0){
        gameFacade.addNeighbor(playerState.state, true)
        map.getNeighbors(playerState.state).foreach(gameFacade.addNeighbor(_, false))
        gameFacade.maxAvailableTroops = gameFacade.troopsToDeploy
      } else {
        map.getNeighbors(playerState.state).foreach(gameFacade.addNeighbor(_, true))
        gameFacade.maxAvailableTroops = playerState.troops - 1
      }
    } else {
      gameFacade.visible = false
    }
  }

  @JSExport
  def clickedState(nameState: String, namePlayer: String, gameFacade: GameFacade): Unit = {
    println("nameState: "+ nameState)
    println("namePlayer: "+ namePlayer)
    println("playerStates " + playerStates)
    playerStates.find(_.state == nameState)
      .foreach(ps => setStateInfo(ps, gameFacade, ps.owner.nickname == namePlayer))
  }

  @JSExport
  def setupGame(gameInfo: String, gameFacade: GameFacade): Unit = {
    val game = Parser.retrieveMessage(gameInfo, GameFullInfo.GameFullInfoCodecJson.Decoder).get
    map = game.map
    playerStates = game.playerStates

    game.players.foreach(pl => gameFacade.addPlayer(pl, game.actualPlayer == pl))
    playerStates.foreach(ps => gameFacade.setPlayerState(ps))
    map.regions.foreach(r => r.states.foreach(s => gameFacade.setStateRegion(s,r.name)))

    gameFacade.setObjective(game.personalInfo.objective.info)

    val cardOccurrence = game.personalInfo.cards.groupBy(identity).mapValues(_.size)
    gameFacade.setCardInfo(cardOccurrence.getOrElse(Infantry, 0),
      cardOccurrence.getOrElse(Cavalry, 0),
      cardOccurrence.getOrElse(Artillery, 0))
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
        gameFacade.notifyGameError(errorMsg.error)
      }
      case "GameUpdate" => {
        println("received GameUpdate ")
        val gameUpdate =
          Parser.retrieveMessage(wrappedMsg.payload, GameUpdate.GameUpdateCodecJson.Decoder).get
        println("Ended parser retrieve message")

        playerStates = gameUpdate.playerStates
        gameFacade.setCurrentPlayer(gameUpdate.actualPlayer)
        gameFacade.troopsToDeploy = gameUpdate.troopsToDeploy
        val cardOccurrence = gameUpdate.personalInfo.cards.groupBy(identity).mapValues(_.size)
        gameFacade.setCardInfo(cardOccurrence.getOrElse(Infantry, 0),
          cardOccurrence.getOrElse(Cavalry, 0),
          cardOccurrence.getOrElse(Artillery, 0))
      }
    }
  }
}
