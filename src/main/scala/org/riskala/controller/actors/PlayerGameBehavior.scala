package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages.GameMessage
import org.riskala.utils.Parser
import org.riskala.view.messages.ToClientMessages
import org.riskala.view.messages.ToClientMessages.{GameFullInfo, GameUpdate}

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
          context.log.info(s"PlayerGameActor of $username received socket payload: $payload")
          val wrappedOpt = Parser.retrieveWrapped(payload)
          if(wrappedOpt.isDefined) {
            val wrapped = wrappedOpt.get
            wrapped.classType match {
              case "ActionMessage" =>
                context.log.info("PlayerGameActor received ActionMessage")
                nextBehavior()
              case "RedeemBonusMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                nextBehavior()
              case "EndTurnMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                nextBehavior()
              case "LeaveMessage" =>
                context.log.info("PlayerGameActor received LeaveMessage")
                nextBehavior()
              case "LogoutMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                Behaviors.stopped
            }
          } else {
            context.log.info("PlayerGameActor failed to retrieve message, IGNORED")
            nextBehavior()
          }
        case GameInfoMessage(players, actualPlayer, troopsToDeploy, map, playerState, personalInfo) =>
          context.log.info(s"PlayerGameActor of $username received GameInfoMessage")
          val fullInfo = GameFullInfo(players, actualPlayer, troopsToDeploy, map, playerState, personalInfo)
          socket ! TextMessage(Parser.wrap("GameFullInfo",fullInfo,GameFullInfo.GameFullInfoCodecJson.Encoder))
          nextBehavior()
        case GameUpdateMessage(actualPlayer, troopsToDeploy, playerStates, personalInfo) =>
          context.log.info(s"PlayerGameActor of $username received GameUpdateMessage")
          val updateInfo = GameUpdate(actualPlayer, troopsToDeploy, playerStates, personalInfo)
          socket ! TextMessage(Parser.wrap("GameUpdate",updateInfo,GameUpdate.GameUpdateCodecJson.Encoder))
          nextBehavior()
        case LobbyReferent(lobby) =>
          context.log.info("LobbyReferent")
          PlayerLobbyBehavior(username,lobby,socket)
        case errorMessage: PlayerMessages.ErrorMessage =>
          context.log.info("ErrorMessage")
          val clientError = ToClientMessages.ErrorMessage(errorMessage.error)
          socket ! TextMessage(Parser.wrap("ErrorMessage",
            clientError,
            ToClientMessages.ErrorMessage.ErrorCodecJson.Encoder))
          nextBehavior()
      }
    }
}
