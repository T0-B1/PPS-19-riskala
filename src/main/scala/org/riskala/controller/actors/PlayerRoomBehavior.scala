package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.riskala.controller.actors.PlayerMessages.{ErrorMessage, GameReferent, LobbyReferent, PlayerMessage, RoomInfoMessage, SocketMessage}
import org.riskala.model.ModelMessages.RoomMessage

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
      case SocketMessage(payload) => nextBehavior()
      case RoomInfoMessage(roomInfo) => nextBehavior()
      case LobbyReferent(lobby) => nextBehavior()
      case GameReferent(game) => nextBehavior()
      //case ReadyMessage() => nextBehavior()
      //case LeaveMessage() => nextBehavior()
      //case LogoutMessage() => nextBehavior()
      case ErrorMessage(error) => nextBehavior()
    }
  }
}
