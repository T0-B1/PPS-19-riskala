package org.riskala.client.creation

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

/**
 * #scala.js
 */
@js.native
@JSGlobal
class CreateRoomFacade extends js.Object {
  def notifyCreateError(error: String): Unit = js.native
  def goToRoom(roomInfo: String): Unit = js.native
  def updateLobby(lobbyInfo: String): Unit = js.native
}
