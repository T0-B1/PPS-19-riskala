package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.riskala.controller.actors.PlayerMessages.{ErrorMessage, GameInfoMessage, LobbyReferent, PlayerMessage, SocketMessage}
import org.riskala.model.ModelMessages.GameMessage

object PlayerGameBehavior {
  def apply(username: String, game: ActorRef[GameMessage], socket: actor.ActorRef): Behavior[PlayerMessage] = {
    playerRoomBehavior(username, game, socket)
  }

  private def playerRoomBehavior(username: String,
                                 game: ActorRef[GameMessage],
                                 socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>
      def nextBehavior(nextUsername: String = username,
                       nextGame: ActorRef[GameMessage] = game,
                       nextSocket: actor.ActorRef = socket): Behavior[PlayerMessage] =
        playerRoomBehavior(nextUsername,nextGame,nextSocket)

      message match {
        case SocketMessage(payload) => nextBehavior()
        //case ActionMessage(from,to,attacking,defending,invading) => nextBehavior()
        case GameInfoMessage() => nextBehavior()
        case LobbyReferent(lobby) => nextBehavior()
        //case LeaveMessage() => nextBehavior()
        //case LogoutMessage() => nextBehavior()
        case ErrorMessage(error) => nextBehavior()
      }
    }
}
