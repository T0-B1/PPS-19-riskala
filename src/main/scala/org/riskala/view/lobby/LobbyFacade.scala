package org.riskala.view.lobby

import org.riskala.view.messages.ToClientMessages.LobbyInfo

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class LobbyFacade extends js.Object {
  def updateLobbyInfo(lobbyInfo: LobbyInfo): Unit = js.native
  def notifyError(error: String): Unit = js.native
}
