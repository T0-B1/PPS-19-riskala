package org.riskala.view.create

import argonaut.Argonaut._
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.CreateMessage
import org.riskala.view.messages.ToClientMessages.ErrorMessage
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientCreateRoom")
object ClientCreateRoom {

  @JSExport
  def getCreateMsgWrapped(name: String, maxPlayer: Int, scenario: String): String = {
    WrappedMessage("CreateMessage",CreateMessage(name,maxPlayer,scenario).asJson.pretty(nospace)).asJson.pretty(nospace)
  }

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
