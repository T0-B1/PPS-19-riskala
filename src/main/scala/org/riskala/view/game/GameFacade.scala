package org.riskala.view.game

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class GameFacade extends js.Object {
  def cleanGame(): Unit = js.native
  def getStateInfo(name: String): Unit = js.native
  def addPlayers(player:String): Unit = js.native
}
