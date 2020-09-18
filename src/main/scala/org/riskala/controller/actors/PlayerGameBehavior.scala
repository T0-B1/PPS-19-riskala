package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.javadsl.model.ws.TextMessage
import jdk.nashorn.internal.runtime.ParserException
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.modelToFix.ModelMessages.GameMessage
import org.riskala.utils.Parser
import org.riskala.view.messages.ToClientMessages.GameFullInfo

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
        case SocketMessage(payload) =>
          context.log.info("SocketMessage")
          nextBehavior()
        //case ActionMessage(from,to,attacking,defending,invading) => nextBehavior()
        case GameInfoMessage(players, actualPlayer, troopsToDeploy, map, playerState, personalInfo) =>
          context.log.info("GameInfoMessage")
          val tmp = GameFullInfo(players, actualPlayer, troopsToDeploy, map, playerState, personalInfo)
          nextBehavior()
        case GameUpdateMessage(actualPlayer, troopsToDeploy, playerStates, personalInfo) =>
          context.log.info("GameUpdateMessage")
          nextBehavior()
        case LobbyReferent(lobby) =>
          context.log.info("LobbyReferent")
          nextBehavior()
        //case LeaveMessage() => nextBehavior()
        //case LogoutMessage() => nextBehavior()
        case ErrorMessage(error) =>
          context.log.info("ErrorMessage")
          nextBehavior()
      }
    }
}
