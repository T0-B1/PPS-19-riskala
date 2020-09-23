package org.riskala.client.creation

import argonaut.Argonaut._
import org.riskala.utils.Parser
import org.riskala.client.messages.FromClientMessages.CreateMessage
import org.riskala.client.messages.ToClientMessages.ErrorMessage
import org.riskala.client.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
 * #scala.js
 */
@JSExportTopLevel("ClientCreateRoom")
object ClientCreateRoom {

  /**
   * Method that gives a wrapped Create message in JSON-format string
   * */
  @JSExport
  def getCreateMsgWrapped(name: String, maxPlayer: Int, scenario: String): String = {
    WrappedMessage("CreateMessage",CreateMessage(name,maxPlayer,scenario).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

  /**
   * Method used to menage messages that are sent to createRoom page
   * */
  @JSExport
  def handleCreateMessage(message: String, createRoomFacade: CreateRoomFacade): Unit = {
    println(s"inside handleCreate. Message = $message")
    val wrappedMsg = Parser.retrieveWrapped(message).get
    println(s"wrappedMessage = $wrappedMsg")
    wrappedMsg.classType match {
      case "RoomInfo" =>
        createRoomFacade.goToRoom(wrappedMsg.payload)
      case "LobbyInfo" =>
        createRoomFacade.updateLobby(wrappedMsg.payload)
      case "ErrorMessage" =>
        println("received error message")
        val errorMsg = Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        createRoomFacade.notifyCreateError(errorMsg.error)
        
      case unhandled => println(s"Ignored message: $unhandled")
    }
  }
}
