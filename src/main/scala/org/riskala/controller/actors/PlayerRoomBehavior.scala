package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.PlayerMessages.{ErrorMessage, GameReferent, LobbyReferent, PlayerMessage, RoomInfoMessage, SocketMessage}
import org.riskala.model.ModelMessages.{Logout, RoomMessage}
import org.riskala.model.room.RoomMessages.{Leave, Ready, UnReady}
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
        context.log.info(s"PlayerActor of $username received socket payload: $payload")
        val wrappedOpt = Parser.retrieveWrapped(payload)
        if(wrappedOpt.isDefined) {
          val wrapped = wrappedOpt.get
          wrapped.classType match {
            case "ReadyMessage" =>
              context.log.info("PlayerRoomActor received ReadyMessage")
              room ! Ready(username, context.self)
              nextBehavior()
            case "LeaveMessage" =>
              context.log.info("PlayerRoomActor received LeaveMessage")
              room ! Leave(context.self)
              nextBehavior()
            case "LogoutMessage" =>
              context.log.info("PlayerRoomActor received LogoutMessage")
              room ! Logout(context.self)
              //TODO: close socket
              Behaviors.stopped
            case "UnReadyMessage" =>
              context.log.info("PlayerRoomActor received UnReadyMessage")
              room ! UnReady(username, context.self)
              nextBehavior()
            case _ =>
              context.log.info("PlayerRoomActor received an unhandled message, IGNORED")
              nextBehavior()
          }
        } else {
          context.log.info("PlayerLobbyActor failed to retrieve message, IGNORED")
          nextBehavior()
        }
      case RoomInfoMessage(roomInfo) =>
        context.log.info(s"PlayerActor of $username received RoomInfoMessage")
        socket ! TextMessage(Parser.wrap("RoomInfo",roomInfo,RoomInfo.RoomInfoCodecJson.Encoder))
        nextBehavior()
      case LobbyReferent(lobby) =>
        context.log.info(s"PlayerActor of $username received LobbyReferent")
        PlayerLobbyBehavior(username,lobby,socket)
      case GameReferent(game) =>
        context.log.info(s"PlayerActor of $username received GameReferent")
        PlayerGameBehavior(username,game,socket)
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
