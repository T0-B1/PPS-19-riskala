package org.riskala.client.lobby

import org.riskala.utils.Parser
import org.riskala.client.messages.ToClientMessages.{ErrorMessage, LobbyInfo}
import argonaut.Argonaut._
import org.riskala.client.messages.FromClientMessages.JoinMessage
import org.riskala.client.messages.WrappedMessage
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
 * #scala.js
 */
@JSExportTopLevel("ClientLobby")
object ClientLobby {

  /**
   * Method that gives a wrapped join message in JSON-format string
   * */
  @JSExport
  def getJoinMsgWrapped(roomName: String): String = {
    WrappedMessage("JoinMessage",JoinMessage(roomName).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  /**
   * Initial lobby setup with its information
   * */
  @JSExport
  def setupLobby(lobbyInfoStr: String, lobbyFacade: LobbyFacade): Unit = {
    val lobbyInfoMsg =
      Parser.retrieveMessage(lobbyInfoStr, LobbyInfo.LobbyInfoCodecJson.Decoder).get
    println("Ended parser retrieve message")
    lobbyFacade.cleanLobby()
    if(lobbyInfoMsg.rooms.isEmpty)
      lobbyFacade.addRoom("","")
    lobbyInfoMsg.rooms.foreach(r=>lobbyFacade.addRoom(r.name,r.players))
    if(lobbyInfoMsg.games.isEmpty)
      lobbyFacade.addGame("")
    lobbyInfoMsg.games.foreach(g=>lobbyFacade.addGame(g))
    if(lobbyInfoMsg.terminatedGames.isEmpty)
      lobbyFacade.addTerminated("")
    lobbyInfoMsg.terminatedGames.foreach(t=>lobbyFacade.addTerminated(t))
  }

  /**
   * Method used to menage messages that are sent to lobby page
   * */
  @JSExport
  def handleLobbyMessage(message: String, lobbyFacade: LobbyFacade): Unit = {
    println(s"inside handleLobby. Message = $message")
    val wrappedMsg = Parser.retrieveWrapped(message).get
    println(s"wrappedMessage = $wrappedMsg")
    wrappedMsg.classType match {
      case "LobbyInfo" =>
        println("case lobbyInfo inside handleLobby")
        setupLobby(wrappedMsg.payload, lobbyFacade)
      case "ErrorMessage" =>
        val errorMsg = Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        lobbyFacade.notifyError(errorMsg.error)
      case "RoomInfo" =>
        lobbyFacade.goToRoom(wrappedMsg.payload)
      case "GameFullInfo" =>
        lobbyFacade.goToGame(wrappedMsg.payload)
      case unhandled => println(s"Ignored message: $unhandled")
    }
  }
}
