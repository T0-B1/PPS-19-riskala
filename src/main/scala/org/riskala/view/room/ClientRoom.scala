package org.riskala.view.room

import argonaut.Argonaut._
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.ReadyMessage
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, RoomInfo}
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientRoom")
object ClientRoom {

  /**
   * Method that gives a wrapped message in JSON-format string
   * */
  @JSExport
  def getMsgWrapped(typeMsg: String): String = {
    WrappedMessage(typeMsg, "").asJson.pretty(nospace)
  }

  /**
   * Method that gives a wrapped ready message in JSON-format string
   * */
  @JSExport
  def getReadyMsgWrapped(color: String): String = {
    WrappedMessage("ReadyMessage", ReadyMessage(color).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  /**
   * Initial setup of the room with its information
   * */
  @JSExport
  def setupRoom(roomInfo: String, roomFacade: RoomFacade): Unit = {
    val room = Parser.retrieveMessage(roomInfo, RoomInfo.RoomInfoCodecJson.Decoder).get
    roomFacade.setName(room.basicInfo.name)
    roomFacade.clearPlayer()
    room.players.foreach(pl => roomFacade.addPlayers(pl.nickname))
  }

  /**
   * Method used to menage messages that are sent to room
   * */
  @JSExport
  def handleRoomMessage(message: String, roomFacade: RoomFacade): Unit = {
    println(s"inside handleRoom.")
    val wrappedMsg = Parser.retrieveWrapped(message).get
    println(s"wrappedMessage")
    wrappedMsg.classType match {
      case "RoomInfo" =>
        println("case roomInfo inside handleLobby")
        val roomInfoMsg =
          Parser.retrieveMessage(wrappedMsg.payload, RoomInfo.RoomInfoCodecJson.Decoder).get
        println("Ended parser retrieve message")
        roomFacade.clearPlayer()
        roomInfoMsg.players.foreach(player=>roomFacade.addPlayers(player.nickname))
      case "ErrorMessage" =>
        println("received error message")
        val errorMsg = Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        roomFacade.notifyError(errorMsg.error)
      case "GameFullInfo" =>
        println("received GameFullInfo")
        roomFacade.goToGame(wrappedMsg.payload)
      case "LobbyInfo" =>
        println("received LobbyInfo")
        roomFacade.goToLobby(wrappedMsg.payload)
      case unhandled => println(s"Ignored message: $unhandled")
    }

  }

}
