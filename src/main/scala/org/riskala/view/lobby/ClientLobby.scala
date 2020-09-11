package org.riskala.view.lobby

import org.riskala.controller.actors.PlayerMessages.ErrorMessage
import org.riskala.model.lobby.LobbyMessages.LobbyInfo
import org.riskala.utils.Parser

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientLobby")
object ClientLobby {
  @JSExport
  def handleLobbyMessage(message: String, lobbyFacade: LobbyFacade): Unit = {
    val wrappedMsg = Parser.retrieveWrapped(message).get
    wrappedMsg.classType match {
      case "LobbyInfo" => {
        val lobbyInfoMsg =
          Parser.retrieveMessage(wrappedMsg.payload, LobbyInfo.LobbyInfoCodecJson.Decoder).get
        lobbyFacade.updateLobbyInfo(lobbyInfoMsg)
      }
      case "ErrorMessage" => {
        val errorMsg = Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        lobbyFacade.notifyError(errorMsg.error)
      }
      case unhandled => println(s"Ignored message: $unhandled")
    }
  }
}
