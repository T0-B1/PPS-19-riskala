package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages.{LobbyMessage, Logout}
import org.riskala.model.lobby.LobbyMessages.{CreateRoom, JoinTo, Subscribe}
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.{CreateMessage, JoinMessage, LogoutMessage}
import org.riskala.view.messages.ToClientMessages.{LobbyInfo, RoomBasicInfo, RoomInfo}
import org.riskala.view.messages.ToClientMessages

object PlayerLobbyBehavior {

  def apply(username: String, lobby: ActorRef[LobbyMessage], socket: actor.ActorRef): Behavior[PlayerMessage] = {
    Behaviors.setup { context =>
      context.log.info("PlayerLobbyBehavior subscribing to Lobby")
      lobby ! Subscribe(context.self)
      playerActor(username, lobby, socket)
    }
  }

  private def playerActor(username: String,
                          lobby: ActorRef[LobbyMessage],
                          socket: actor.ActorRef): Behavior[PlayerMessage] = {
    Behaviors.receive { (context,message) =>

      message match {

        case SocketMessage(payload) =>
          context.log.info(s"PlayerActor of $username received socket payload: $payload")
          val wrappedOpt = Parser.retrieveWrapped(payload)
          if(wrappedOpt.isDefined) {
            val wrapped = wrappedOpt.get
            wrapped.classType match {

              case "JoinMessage" =>
                context.log.info("PlayerLobbyActor received JoinMessage")
                Parser.retrieveMessage(wrapped.payload, JoinMessage.JoinCodecJson.Decoder)
                  .foreach(j => lobby ! JoinTo(context.self, j.name))
                Behaviors.same

              case "CreateMessage" =>
                context.log.info("PlayerLobbyActor received CreateMessage")
                Parser.retrieveMessage(wrapped.payload, CreateMessage.CreateCodecJson.Decoder)
                  .foreach(r =>
                    lobby ! CreateRoom(context.self, RoomInfo(RoomBasicInfo(r.name, 0, r.maxPlayer), Set.empty, r.scenario)))
                Behaviors.same

              case "LogoutMessage" =>
                context.log.info("PlayerLobbyActor received LogoutMessage")
                lobby ! Logout(context.self)
                Behaviors.stopped

              case _ =>
                context.log.info("PlayerLobbyActor received an unhandled message, IGNORED")
                Behaviors.same

            }
          } else {
            context.log.info("PlayerLobbyActor failed to retrieve message, IGNORED")
            Behaviors.same
          }

        case LobbyInfoMessage(lobbyInfo) =>
          context.log.info(s"PlayerActor of $username received LobbyInfoMessage")
          socket ! TextMessage(Parser.wrap("LobbyInfo",lobbyInfo,LobbyInfo.LobbyInfoCodecJson.Encoder))
          Behaviors.same

        case errorMessage: PlayerMessages.ErrorMessage =>
          context.log.info(s"PlayerActor of $username received ErrorMessage")
          val clientError = ToClientMessages.ErrorMessage(errorMessage.error)
          socket ! TextMessage(Parser.wrap("ErrorMessage",
            clientError,
            ToClientMessages.ErrorMessage.ErrorCodecJson.Encoder))
          Behaviors.same

        case RegisterSocket(newSocketActor) =>
          context.log.info("registering new socket")
          playerActor(username, lobby, newSocketActor)

        case RoomReferent(room) =>
          context.log.info(s"PlayerActor of $username received RoomReferent")
          PlayerRoomBehavior(username,room,socket)

        case GameReferent(game) =>
          context.log.info(s"PlayerActor of $username received GameReferent")
          PlayerGameBehavior(username,game,socket)

        case x =>
          context.log.info(s"PlayerActor of $username received "+ x +", IGNORED")
          Behaviors.same

      }
    }
  }
}