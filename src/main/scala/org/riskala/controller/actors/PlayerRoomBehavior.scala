package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.PlayerMessages.{ErrorMessage, GameReferent, LobbyReferent, PlayerMessage, RoomInfoMessage, SocketMessage}
import org.riskala.model.ModelMessages.RoomMessage
import org.riskala.utils.Parser
import org.riskala.view.messages.ToClientMessages.RoomInfo
import org.riskala.view.messages.ToClientMessages

object PlayerRoomBehavior {
  def apply(username: String, room: ActorRef[RoomMessage], socket: actor.ActorRef): Behavior[PlayerMessage] = {
    playerRoomBehavior(username, room, socket)
  }

  private def playerRoomBehavior(username: String,
                                 room: ActorRef[RoomMessage],
                                 socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>
    def nextBehavior(nextUsername: String = username,
                     nextRoom: ActorRef[RoomMessage] = room,
                     nextSocket: actor.ActorRef = socket): Behavior[PlayerMessage] =
      playerRoomBehavior(nextUsername,nextRoom,nextSocket)

    message match {
      case SocketMessage(payload) =>
        //TODO
        nextBehavior()

      case RoomInfoMessage(roomInfo) =>
        context.log.info(s"PlayerActor of $username received RoomInfoMessage")
        socket ! TextMessage(Parser.wrap("RoomInfo",roomInfo,RoomInfo.RoomInfoCodecJson.Encoder))
        nextBehavior()

      case LobbyReferent(lobby) =>
        //TODO
        nextBehavior()
      case GameReferent(game) =>
        context.log.info(s"PlayerActor of $username received GameReferent")
        PlayerGameBehavior(username,game,socket)
      //case ReadyMessage() => nextBehavior()
      //case LeaveMessage() => nextBehavior()
      //case LogoutMessage() => nextBehavior()
      case errorMessage: PlayerMessages.ErrorMessage =>
        context.log.info(s"PlayerActor of $username received ErrorMessage")
        val clientError = ToClientMessages.ErrorMessage(errorMessage.error)
        socket ! TextMessage(Parser.wrap("ErrorMessage",
          clientError,
          ToClientMessages.ErrorMessage.ErrorCodecJson.Encoder))
        nextBehavior()
    }
  }
}
