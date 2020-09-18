package org.riskala.view.game

import org.riskala.model.{MapGeography, PlayerState}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class GameFacade extends js.Object {
  var state: String = js.native
  var owner: String = js.native
  var troops: Int = js.native
  var region: String = js.native
  var maxAvailableTroops: Int = js.native
  var troopsToDeploy: Int = js.native
  var visible: Boolean = js.native
  var nameBtn: String = js.native

  def addPlayer(player:String, myTurn: Boolean): Unit = js.native
  def setMap(map: MapGeography): Unit = js.native //Così chiamo getNeighbors
  def cleanPlayerState(): Unit = js.native //pulisce la lista di dx
  def addPlayerState(playerState: PlayerState): Unit = js.native //popola la lista di dx
  def loadObjectives(objective: String): Unit = js.native
  def setCardInfo(infantry: Int, cavalry: Int, artillery: Int): Unit = js.native
  def notifyGameError(error: String): Unit = js.native
  def addNeighbor(neighbor: String, checked: Boolean): Unit = js.native


  def updateInfo(playerTurn: String): Unit = js.native //Cosa devo prendere?
}
