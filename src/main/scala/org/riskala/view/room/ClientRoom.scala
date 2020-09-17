package org.riskala.view.room

import argonaut.Argonaut._
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.ReadyMessage
import org.riskala.view.messages.ToClientMessages.{ErrorMessage, RoomInfo}
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientRoom")
object ClientRoom {

  @JSExport
  def getMsgWrapped(typeMsg: String): String = {
    WrappedMessage(typeMsg, "").asJson.pretty(nospace)
  }

  @JSExport
  def setupRoom(roomInfo: String, roomFacade: RoomFacade): Unit = {
    val room = Parser.retrieveMessage(roomInfo, RoomInfo.RoomInfoCodecJson.Decoder).get
    roomFacade.setName(room.basicInfo.name)
    roomFacade.clearPlayer()
    room.players.foreach(pl => roomFacade.addPlayers(pl))
    //TODO gestione scenario
  }

  @JSExport
  def handleRoomMessage(message: String, roomFacade: RoomFacade): Unit = {
    println(s"inside handleRoom. Message = $message")
    val wrappedMsg = Parser.retrieveWrapped(message).get
    println(s"wrappedMessage = $wrappedMsg")
    wrappedMsg.classType match {
      case "RoomInfo" => {
        println("case roomInfo inside handleLobby")
        val roomInfoMsg =
          Parser.retrieveMessage(wrappedMsg.payload, RoomInfo.RoomInfoCodecJson.Decoder).get
        println("Ended parser retrieve message")
        roomFacade.clearPlayer()
        roomInfoMsg.players.foreach(player=>roomFacade.addPlayers(player))
      }
      case "ErrorMessage" => {
        println("received error message")
        val errorMsg = Parser.retrieveMessage(wrappedMsg.payload, ErrorMessage.ErrorCodecJson.Decoder).get
        //roomFacade.notifyCreateError(errorMsg.error)
      }
      case unhandled => println(s"Ignored message: $unhandled")
    }

  }

}
