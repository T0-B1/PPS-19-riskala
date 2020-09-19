package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages.{GameMessage, Logout}
import org.riskala.model.game.GameMessages.{Action, EndTurn, GetFullInfo, Leave, RedeemBonus}
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionAttackMessage, ActionDeployMessage, ActionMoveMessage, RedeemBonusMessage}
import org.riskala.view.messages.ToClientMessages
import org.riskala.view.messages.ToClientMessages.{GameFullInfo, GameUpdate}

object PlayerGameBehavior {
  def apply(username: String, game: ActorRef[GameMessage], socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.setup{ context =>
      game ! GetFullInfo(username, context.self)
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
              case "ActionAttackMessage" =>
                context.log.info("PlayerGameActor received ActionAttackMessage")
                val action = Parser.retrieveMessage(wrapped.payload,ActionAttackMessage.ActionAttackMessageCodecJson.Decoder)
                action.foreach(a => game ! Action(username,a.from,a.to,a.troops))
                nextBehavior()
              case "ActionMoveMessage" =>
                context.log.info("PlayerGameActor received ActionMoveMessage")
                val action = Parser.retrieveMessage(wrapped.payload,ActionMoveMessage.ActionMoveMessageCodecJson.Decoder)
                action.foreach(a => game ! Action(username,a.from,a.to,a.troops))
                nextBehavior()
              case "ActionDeployMessage" =>
                context.log.info("PlayerGameActor received ActionMessage")
                val action = Parser.retrieveMessage(wrapped.payload,ActionDeployMessage.ActionDeployMessageCodecJson.Decoder)
                action.foreach(a => game ! Action(username,a.from,a.to,a.troops))
                nextBehavior()
              case "RedeemBonusMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                val redeem = Parser.retrieveMessage(wrapped.payload,RedeemBonusMessage.RedeemBonusCodecJson.Decoder)
                redeem.foreach(r => game ! RedeemBonus(username,r.card))
                nextBehavior()
              case "EndTurnMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                game ! EndTurn(username)
                nextBehavior()
              case "LeaveMessage" =>
                context.log.info("PlayerGameActor received LeaveMessage")
                game ! Leave(context.self)
                nextBehavior()
              case "LogoutMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                game ! Logout(context.self)
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
          context.log.info(s"PlayerGameActor of $username received LobbyReferent")
          PlayerLobbyBehavior(username,lobby,socket)
        case errorMessage: PlayerMessages.ErrorMessage =>
          context.log.info(s"PlayerGameActor of $username received ErrorMessage: ${errorMessage.error}")
          val clientError = ToClientMessages.ErrorMessage(errorMessage.error)
          socket ! TextMessage(Parser.wrap("ErrorMessage",
            clientError,
            ToClientMessages.ErrorMessage.ErrorCodecJson.Encoder))
          nextBehavior()
      }
    }
}
