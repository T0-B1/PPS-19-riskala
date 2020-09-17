package org.riskala.view.game

import org.riskala.model.{MapGeography, PlayerState}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class GameFacade extends js.Object {
  def addPlayer(player:String, myTurn: Boolean): Unit = js.native
  def setMap(map: MapGeography): Unit = js.native //Così chiamo getNeighbors
  def cleanPlayerState(): Unit = js.native
  def addPlayerState(playerState: PlayerState): Unit = js.native
  def loadObjectives(objective: String): Unit = js.native
  def loadCard(infantry: Int, cavalry: Int, artillery: Int): Unit = js.native


  def updateInfo(playerTurn: String): Unit = js.native //Cosa devo prendere?
}
