package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages.{LobbyMessage, Logout}
import org.riskala.model.lobby.LobbyMessages.{CreateRoom, JoinTo, LobbyInfo}
import org.riskala.model.room.RoomMessages.{RoomBasicInfo, RoomInfo}
import org.riskala.utils.Parser
import argonaut.Argonaut._

object PlayerLobbyBehavior {

  def apply(username: String, lobby: ActorRef[LobbyMessage] = null, socket: actor.ActorRef): Behavior[PlayerMessage] = {
    playerActor(username, lobby, socket)
  }

  private def playerActor(username: String,
                          lobby: ActorRef[LobbyMessage],
                          socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>

      def nextBehavior(newUsername: String = username,
                       newLobby: ActorRef[LobbyMessage] = lobby,
                       newSocket: actor.ActorRef = socket): Behavior[PlayerMessage] =
        playerActor(newUsername, newLobby, newSocket)

      message match {
        case SocketMessage(payload) =>
          context.log.info(s"PlayerActor of $username received socket payload: $payload")
          val typed = Parser.unwrap(payload)
          typed.classType match {
            case _: Class[JoinMessage] =>
              Parser.retrieveMessage(typed.payload, JoinMessage.JoinCodecJson.Decoder)
                .foreach(j => lobby ! JoinTo(context.self,j.name))
              nextBehavior()
            case _: Class[CreateMessage] =>
              Parser.retrieveMessage(typed.payload, CreateMessage.CreateCodecJson.Decoder)
                .foreach(r =>
                  lobby ! CreateRoom(context.self, RoomInfo(RoomBasicInfo(r.name,0,r.maxPlayer), r.scenario)))
              nextBehavior()
            case _: Class[LogoutMessage] =>
              lobby ! Logout(context.self)
              //TODO: close socket
              Behaviors.stopped
          }

        case LobbyInfoMessage(lobbyInfo) =>
          context.log.info(s"PlayerActor of $username received LobbyInfoMessage")
          socket ! TextMessage(Parser.wrap("LobbyInfo",lobbyInfo,LobbyInfo.LobbyInfoCodecJson.Encoder))
          nextBehavior()
        case error: ErrorMessage =>
          context.log.info(s"PlayerActor of $username received ErrorMessage")
          socket ! TextMessage(Parser.wrap("ErrorMessage",error, ErrorMessage.ErrorCodecJson.Encoder))
          nextBehavior()
        case RegisterSocket(newSocketActor) =>
          context.log.info("registering new socket")
          nextBehavior(newSocket = newSocketActor)

        case x =>
          context.log.info(s"PlayerActor of $username received "+ x +", IGNORED")
          nextBehavior()
      }
    }

}