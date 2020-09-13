package org.riskala.view.lobby

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class LobbyFacade extends js.Object {
  def cleanLobby(): Unit = js.native
  def addRoom(name: String, player:String): Unit = js.native
  def addGame(name: String): Unit = js.native
  def addTerminated(name: String): Unit = js.native
  def goToRoom(roomInfo: String): Unit = js.native
  def notifyError(error: String): Unit = js.native
}
