package org.riskala.view.lobby

import org.riskala.utils.Parser
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, LobbyInfo, RoomNameInfo}
import argonaut.Argonaut._
import org.riskala.view.messages.FromClientMessages.JoinMessage
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientLobby")
object ClientLobby {

  @JSExport
  def getJoinMsgWrapped(roomName: String): String = {
    WrappedMessage("JoinMessage",JoinMessage(roomName).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  @JSExport
  def handleLobbyMessage(message: String, lobbyFacade: LobbyFacade): Unit = {
    println(s"inside handleLobby. Message = $message")
    val wrappedMsg = Parser.retrieveWrapped(message).get
    println(s"wrappedMessage = $wrappedMsg")
    wrappedMsg.classType match {
      case "LobbyInfo" => {
        println("case lobbyInfo inside handleLobby")
        val lobbyInfoMsg =
          Parser.retrieveMessage(wrappedMsg.payload, LobbyInfo.LobbyInfoCodecJson.Decoder).get
        println("Ended parser retrieve message")
        lobbyFacade.cleanLobby()
        lobbyInfoMsg.rooms.foreach(r=>lobbyFacade.addRoom(r.name,r.players))
        lobbyInfoMsg.games.foreach(g=>lobbyFacade.addGame(g))
        lobbyInfoMsg.terminatedGames.foreach(t=>lobbyFacade.addTerminated(t))
      }
      case "ErrorMessage" => {
        val errorMsg = Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        lobbyFacade.notifyError(errorMsg.error)
      }
      case unhandled => println(s"Ignored message: $unhandled")
    }
  }
}
