package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import org.riskala.controller.actors.PlayerMessages._

object PlayerActor {

  def apply(username: String, socket: actor.ActorRef): Behavior[PlayerMessage] = {
    playerActor(username, socket)
  }

  private def playerActor(username: String, socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>

      def nextBehavior(newUsername: String = username, newSocket: actor.ActorRef = socket): Behavior[PlayerMessage] =
        playerActor(newUsername, newSocket)

      message match {
        case SocketMessage(payload) => {
          context.log.info(s"PlayerActor of $username received socket payload: $payload")
          socket ! TextMessage(s"PlayerActor of $username echoing: $payload")
          nextBehavior()
        }
        case RoomInfoMessage(roomInfo) => context.log.info("RoomInfoMessage"); nextBehavior()
        case LobbyInfoMessage(lobbyInfo) => context.log.info("LobbyInfoMessage"); nextBehavior()
        case GameInfoMessage() => context.log.info("GameInfoMessage"); nextBehavior()
        case RoomAlreadyExistsMessage() => context.log.info("RoomAlreadyExistsMessage"); nextBehavior()
        case RoomNotFoundMessage() => context.log.info("RoomNotFoundMessage"); nextBehavior()
        case GameNotFoundMessage() => context.log.info("GameNotFoundMessage"); nextBehavior()
        case RegisterSocket(newSocketActor) => {
          context.log.info("registering new socket")
          nextBehavior(newSocket = newSocketActor)
        }
      }
    }

}