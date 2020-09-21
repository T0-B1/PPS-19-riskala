package org.riskala.view.game

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class GameFacade extends js.Object {
  var myName: String = js.native
  var maxAvailableTroops: Int = js.native
  var visible: Boolean = js.native
  var nameActionBtn: String = js.native
  var selectedNeighbor: String = js.native
  var state: String = js.native

  def addPlayer(player:String, myTurn: Boolean): Unit = js.native
  def setPlayerState(playerState: String, owner: String, troops: Int): Unit = js.native
  def setStateRegion(state: String, region: String): Unit = js.native
  def setObjective(objective: String): Unit = js.native
  def setCardInfo(infantry: Int, cavalry: Int, artillery: Int): Unit = js.native
  def notifyGameError(error: String): Unit = js.native
  def addNeighbor(neighbor: String, checked: Boolean): Unit = js.native
  def setStateInfo(state: String, owner: String, troops: Int, region: String): Unit = js.native
  def setCurrentPlayer(player: String): Unit = js.native
  def setWinner(winner: String): Unit = js.native
  def goToLobby(lobbyInfo: String): Unit = js.native
}
