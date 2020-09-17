package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.javadsl.model.ws.TextMessage
import jdk.nashorn.internal.runtime.ParserException
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages.GameMessage
import org.riskala.utils.Parser

object PlayerGameBehavior {
  def apply(username: String, game: ActorRef[GameMessage], socket: actor.ActorRef): Behavior[PlayerMessage] = {
    playerGameBehavior(username, game, socket)
  }


  private def playerGameBehavior(username: String,
                                 game: ActorRef[GameMessage],
                                 socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>
      def nextBehavior(nextUsername: String = username,
                       nextGame: ActorRef[GameMessage] = game,
                       nextSocket: actor.ActorRef = socket): Behavior[PlayerMessage] =
        playerGameBehavior(nextUsername,nextGame,nextSocket)

      message match {
        case SocketMessage(payload) => nextBehavior()
        //case ActionMessage(from,to,attacking,defending,invading) => nextBehavior()
        case GameInfoMessage(players, actualPlayer, map, playerState, personalInfo) => nextBehavior()
        case GameUpdateMessage(actualPlayer, playerStates, personalInfo) => nextBehavior()
        case LobbyReferent(lobby) => nextBehavior()
        //case LeaveMessage() => nextBehavior()
        //case LogoutMessage() => nextBehavior()
        case ErrorMessage(error) => nextBehavior()
      }
    }
}
