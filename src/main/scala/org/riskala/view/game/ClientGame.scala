package org.riskala.view.game

import argonaut.Argonaut.{ToJsonIdentity, nospace}
import org.riskala.model.Cards._
import org.riskala.model.{MapGeography, PlayerState}
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionMessage, RedeemBonusMessage}
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, GameFullInfo, LobbyInfo}
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
  def getRedeemBonusMsgWrapped(cardType: Cards): String =  {
    WrappedMessage("RedeemBonusMessage", RedeemBonusMessage(cardType).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  @JSExport
  def neighborClick(clickedState: String, namePlayer: String, mapSelectedState: String, gameFacade: GameFacade): Unit = {
    if(clickedState equals mapSelectedState) {
      gameFacade.nameBtn = "Deploy"
    } else{
      playerStates.find(_.state == clickedState)
        .foreach(ps =>
          if(ps.owner.nickname == namePlayer) gameFacade.nameBtn = "Move" else gameFacade.nameBtn = "Attack")
    }
  }

  private def setStateInfo(playerState:PlayerState, gameFacade: GameFacade, myState: Boolean): Unit = {
    println("setStateInfo")
    gameFacade.state = playerState.state
    println("gameFacade.state: " + gameFacade.state)
    gameFacade.owner = playerState.owner.nickname
    println("gameFacade.owner: " + gameFacade.owner)
    gameFacade.troops = playerState.troops
    println("gameFacade.troops: " + gameFacade.troops)
    gameFacade.region = map.regions.find(_.states.contains(playerState.state)).map(_.name).getOrElse("")
    println("gameFacade.region " + gameFacade.region)

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
    }
    gameFacade.visible = false
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
      case "GameFullInfo" => {
        println("received game full info ")
        val gameFullInfo =
          Parser.retrieveMessage(wrappedMsg.payload, GameFullInfo.GameFullInfoCodecJson.Decoder).get
        println("Ended parser retrieve message")
        map = gameFullInfo.map
        playerStates = gameFullInfo.playerStates
        
        gameFullInfo.players.foreach(pl => gameFacade.addPlayer(pl, gameFullInfo.actualPlayer == pl))
        gameFacade.setMap(map)
        gameFacade.cleanPlayerState()
        playerStates.foreach(ps => gameFacade.addPlayerState(ps))
        gameFacade.loadObjectives(gameFullInfo.personalInfo.objective.info)

        val cardOccurrence = gameFullInfo.personalInfo.cards.groupBy(identity).mapValues(_.size)
        gameFacade.setCardInfo(cardOccurrence.getOrElse(Infantry, 0),
          cardOccurrence.getOrElse(Cavalry, 0),
          cardOccurrence.getOrElse(Artillery, 0))

      }
    }
  }
}
