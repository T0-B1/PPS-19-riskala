package org.riskala.view.game

import argonaut.Argonaut.{ToJsonIdentity, nospace}
import org.riskala.model.Cards._
import org.riskala.model.{Cards, MapGeography, PlayerState}
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionAttackMessage, ActionDeployMessage, ActionMoveMessage, RedeemBonusMessage}
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, GameEnd, GameFullInfo, GameUpdate}
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientGame")
object ClientGame {

  private var map: MapGeography = _
  private var playerStates: Set[PlayerState] = _
  private var myTroopsToDeploy: Int = 0
  private var myActualPlayer: String = ""
  private var myIsDeployOnly: Boolean = true

  /**
   * Method that gives a wrapped message in JSON-format string
   * */
  @JSExport
  def getEmptyMsgWrapped(typeMessage: String): String =  {
    WrappedMessage(typeMessage, "").asJson.pretty(nospace)
  }

  /**
   * Method that gives a wrapped action message in JSON-format string
   * @param actionType Attack or Move or Deploy
   * @param from The state of departure
   * @param to The arrival state
   * @param troops The number of troops used for this action
   * */
  @JSExport
  def getActionMsgWrapped(actionType: String, from: String, to: String, troops: Int): String =  {
    val (msg,action) = actionType match {
      case "Attack" => ("ActionAttackMessage", ActionAttackMessage(from, to, troops).asJson.pretty(nospace))
      case "Move" => ("ActionMoveMessage", ActionMoveMessage(from, to, troops).asJson.pretty(nospace))
      case "Deploy" => ("ActionDeployMessage", ActionDeployMessage(from, to, troops).asJson.pretty(nospace))
    }
    WrappedMessage(msg, action).asJson.pretty(nospace)
  }

  /**
   * Method that gives a wrapped redeemBonus message in JSON-format string
   * */
  @JSExport
  def getRedeemBonusMsgWrapped(cardType: String): String =  {
    val card = Cards.withName(cardType)
    WrappedMessage("RedeemBonusMessage", RedeemBonusMessage(card).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  /**
   * Method that gives information about the clicked neighbor
   * @param clickedState The name of the clicked state into the neighbor list
   * @param namePlayer The player who requires the information
   * @param mapSelectedState The name of the clicked state into the map
   * @param gameFacade The instance of javascript facade on which call method
   * */
  @JSExport
  def neighborClick(clickedState: String, namePlayer: String,
                    mapSelectedState: String, gameFacade: GameFacade): Unit = {
    if(clickedState equals mapSelectedState) {
      gameFacade.nameActionBtn = "Deploy"
      gameFacade.maxAvailableTroops = myTroopsToDeploy
    } else{
      playerStates.find(_.state == clickedState)
        .foreach(ps => {
          gameFacade.nameActionBtn = if (ps.owner.nickname == namePlayer) "Move" else "Attack"
          gameFacade.maxAvailableTroops = playerStates.find(_.state == mapSelectedState).get.troops - 1
        }
      )
    }
  }

  private def myStateInfo(playerState:PlayerState, gameFacade: GameFacade, myState: Boolean, myTurn: Boolean): Unit = {
    gameFacade.setStateInfo(playerState.state,
      playerState.owner.nickname,
      playerState.troops,
      map.regions.find(_.states.contains(playerState.state)).map(_.name).getOrElse(""))
    val neighbors =  map.getNeighbors(playerState.state)
    if(myState && myTurn){
      gameFacade.visible = true
      if(myIsDeployOnly) {
        if(myTroopsToDeploy > 0) {
          gameFacade.addNeighbor(playerState.state, true)
          gameFacade.maxAvailableTroops = myTroopsToDeploy
          gameFacade.selectedNeighbor = playerState.state
          neighborClick(playerState.state,gameFacade.myName,playerState.state,gameFacade)
        } else {
          gameFacade.visible = false
        }
      } else {
        if(myTroopsToDeploy > 0){
          gameFacade.addNeighbor(playerState.state, true)
          neighbors.foreach(gameFacade.addNeighbor(_, false))
          gameFacade.maxAvailableTroops = myTroopsToDeploy
          gameFacade.selectedNeighbor = playerState.state
          neighborClick(playerState.state,gameFacade.myName,playerState.state,gameFacade)
        } else {
          val mySelection = neighbors.collectFirst({case s => s}).get
          val remainingNeighbors = neighbors.filterNot(_ == mySelection)
          gameFacade.addNeighbor(mySelection, true)
          remainingNeighbors.foreach(gameFacade.addNeighbor(_, false))
          gameFacade.maxAvailableTroops = playerState.troops - 1
          gameFacade.selectedNeighbor = mySelection
          neighborClick(mySelection,gameFacade.myName,playerState.state,gameFacade)
        }
      }
    } else {
      gameFacade.visible = false
    }
  }

  /**
   * Method that gives information about the clicked state into the map
   * @param nameState The name of the clicked state
   * @param namePlayer The name of the player
   * @param gameFacade The instance of javascript facade on which call method
   * */
  @JSExport
  def clickedState(nameState: String, namePlayer: String, gameFacade: GameFacade): Unit = {
    playerStates.find(_.state == nameState)
      .foreach(ps => myStateInfo(ps, gameFacade, ps.owner.nickname == namePlayer, myActualPlayer == namePlayer))
  }

  /**
   * Initial game setup with its information
   * */
  @JSExport
  def setupGame(gameInfo: String, gameFacade: GameFacade): Unit = {
    val game = Parser.retrieveMessage(gameInfo, GameFullInfo.GameFullInfoCodecJson.Decoder).get
    map = game.map
    playerStates = game.playerStates
    myTroopsToDeploy = game.troopsToDeploy
    myActualPlayer = game.actualPlayer
    myIsDeployOnly = game.isDeployOnly
    game.players.foreach(pl => gameFacade.addPlayer(pl, game.actualPlayer == pl))
    playerStates.foreach(ps => gameFacade.setPlayerState(ps.state, ps.owner.nickname, ps.troops))
    map.regions.foreach(r => r.states.foreach(s => gameFacade.setStateRegion(s,r.name)))
    gameFacade.setObjective(game.personalInfo.objective.info)
    val cardOccurrence = game.personalInfo.cards.groupBy(identity).mapValues(_.size)
    gameFacade.setCardInfo(cardOccurrence.getOrElse(Infantry, 0),
      cardOccurrence.getOrElse(Cavalry, 0),
      cardOccurrence.getOrElse(Artillery, 0))
    game.winner.foreach(winner => gameFacade.setWinner(winner.nickname))
  }

  /**
   * Method used to menage messages that are sent to game page
   * */
  @JSExport
  def handleGameMessage(message: String, gameFacade: GameFacade): Unit = {
    val wrappedMsg = Parser.retrieveWrapped(message).get
    wrappedMsg.classType match {
      case "ErrorMessage" => {
        println("received error message")
        val errorMsg =
          Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        gameFacade.notifyGameError(errorMsg.error)
      }
      case "GameUpdate" =>
        println("received GameUpdate ")
        val gameUpdate =
          Parser.retrieveMessage(wrappedMsg.payload, GameUpdate.GameUpdateCodecJson.Decoder).get
        println("Ended parser retrieve message")
        playerStates = gameUpdate.playerStates
        playerStates.foreach(ps => gameFacade.setPlayerState(ps.state, ps.owner.nickname, ps.troops))
        myTroopsToDeploy = gameUpdate.troopsToDeploy
        myActualPlayer = gameUpdate.actualPlayer
        myIsDeployOnly = gameUpdate.isDeployOnly
        gameFacade.maxAvailableTroops = gameUpdate.troopsToDeploy
        gameFacade.setCurrentPlayer(gameUpdate.actualPlayer)
        val cardOccurrence = gameUpdate.personalInfo.cards.groupBy(identity).mapValues(_.size)
        gameFacade.setCardInfo(cardOccurrence.getOrElse(Infantry, 0),
          cardOccurrence.getOrElse(Cavalry, 0),
          cardOccurrence.getOrElse(Artillery, 0))
        if(gameFacade.state!="Select a state")
          clickedState(gameFacade.state,gameFacade.myName,gameFacade)

      case "GameEnd" =>
        val winner =
          Parser.retrieveMessage(wrappedMsg.payload, GameEnd.GameEndCodecJson.Decoder).get.winner
        gameFacade.setWinner(winner.nickname)

      case "LobbyInfo" =>
        gameFacade.goToLobby(wrappedMsg.payload)
    }
  }
}
