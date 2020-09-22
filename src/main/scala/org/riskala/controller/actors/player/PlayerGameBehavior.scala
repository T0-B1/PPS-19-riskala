package org.riskala.controller.actors.player

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.player.PlayerMessages.PlayerMessage
import org.riskala.model.ModelMessages.{GameMessage, Logout}
import org.riskala.model.game.GameMessages._
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{ActionAttackMessage, ActionDeployMessage, ActionMoveMessage, RedeemBonusMessage}
import org.riskala.view.messages.ToClientMessages
import org.riskala.view.messages.ToClientMessages.{GameEnd, GameFullInfo, GameUpdate}

object PlayerGameBehavior {

  /**
   * Creates a PlayerGameBehavior which handles game messages
   * @param username Username of the player
   * @param game The actorRef of the gameManager
   * @param socket Classic Akka Actor which handles a socket
   * @return A new PlayerGameBehavior
   * */
  def apply(username: String, game: ActorRef[GameMessage], socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.setup{ context =>
      game ! GetFullInfo(username, context.self)
      playerGameBehavior(username, game, socket)
  }

  private def playerGameBehavior(username: String,
                                 game: ActorRef[GameMessage],
                                 socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>

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
                action.foreach(a => game ! ActionAttack(username,a.from,a.to,a.troops))
                Behaviors.same

              case "ActionMoveMessage" =>
                context.log.info("PlayerGameActor received ActionMoveMessage")
                val action = Parser.retrieveMessage(wrapped.payload,ActionMoveMessage.ActionMoveMessageCodecJson.Decoder)
                action.foreach(a => game ! ActionMove(username,a.from,a.to,a.troops))
                Behaviors.same

              case "ActionDeployMessage" =>
                context.log.info("PlayerGameActor received ActionMessage")
                val action = Parser.retrieveMessage(wrapped.payload,ActionDeployMessage.ActionDeployMessageCodecJson.Decoder)
                action.foreach(a => game ! ActionDeploy(username,a.from,a.to,a.troops))
                Behaviors.same

              case "RedeemBonusMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                val redeem = Parser.retrieveMessage(wrapped.payload,RedeemBonusMessage.RedeemBonusCodecJson.Decoder)
                redeem.foreach(r => game ! RedeemBonus(username,r.card))
                Behaviors.same

              case "EndTurnMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                game ! EndTurn(username)
                Behaviors.same

              case "LeaveMessage" =>
                context.log.info("PlayerGameActor received LeaveMessage")
                game ! Leave(context.self)
                Behaviors.same

              case "LogoutMessage" =>
                context.log.info("PlayerGameActor received RedeemBonusMessage")
                game ! Logout(context.self)
                Behaviors.stopped

            }
          } else {
            context.log.info("PlayerGameActor failed to retrieve message, IGNORED")
            Behaviors.same
          }

        case GameInfoMessage(players, actualPlayer, troopsToDeploy, map, isDeployOnly, playerState, personalInfo, winner) =>
          context.log.info(s"PlayerGameActor of $username received GameInfoMessage")
          val fullInfo = GameFullInfo(players, actualPlayer, troopsToDeploy, isDeployOnly, map, playerState, personalInfo, winner)
          socket ! TextMessage(Parser.wrap("GameFullInfo",fullInfo,GameFullInfo.GameFullInfoCodecJson.Encoder))
          Behaviors.same

        case GameUpdateMessage(actualPlayer, troopsToDeploy, isDeployOnly, playerStates, personalInfo) =>
          context.log.info(s"PlayerGameActor of $username received GameUpdateMessage")
          val updateInfo = GameUpdate(actualPlayer, troopsToDeploy, isDeployOnly, playerStates, personalInfo)
          socket ! TextMessage(Parser.wrap("GameUpdate",updateInfo,GameUpdate.GameUpdateCodecJson.Encoder))
          Behaviors.same

        case GameEndMessage(winner) =>
          context.log.info(s"PlayerGameActor of $username received EndGameMessage")
          socket ! TextMessage(Parser.wrap("GameEnd",GameEnd(winner),GameEnd.GameEndCodecJson.Encoder))
          Behaviors.same

        case LobbyReferent(lobby) =>
          context.log.info(s"PlayerGameActor of $username received LobbyReferent")
          PlayerLobbyBehavior(username,lobby,socket)

        case errorMessage: PlayerMessages.ErrorMessage =>
          context.log.info(s"PlayerGameActor of $username received ErrorMessage: ${errorMessage.error}")
          val clientError = ToClientMessages.ErrorMessage(errorMessage.error)
          socket ! TextMessage(Parser.wrap("ErrorMessage",
            clientError,
            ToClientMessages.ErrorMessage.ErrorCodecJson.Encoder))
          Behaviors.same

        case RegisterSocket(newSocketActor) =>
          context.log.info(s"PlayerActor of $username registering new socket")
          PlayerGameBehavior(username, game, newSocketActor)

        case x =>
          context.log.info(s"PlayerActor of $username received "+ x +", IGNORED")
          Behaviors.same

      }
    }
}
