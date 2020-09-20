package org.riskala.view.room
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class RoomFacade extends js.Object{
  def addPlayers(name:String): Unit = js.native
  def clearPlayer(): Unit = js.native
  def setName(roomName: String): Unit = js.native
  def goToGame(gameInfo: String): Unit = js.native
  def goToLobby(lobbyInfo: String): Unit = js.native
  def notifyError(error: String): Unit = js.native
}
