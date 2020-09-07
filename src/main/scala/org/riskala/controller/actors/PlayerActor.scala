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
      message match {
        case SocketMessage(payload) => {
          context.log.info(s"PlayerActor of $username received socket payload: $payload")
          socket ! TextMessage(s"PlayerActor of $username echoing: $payload")
          Behaviors.same
          case RegisterSocket(socket) => context.log.info("RegisterSocket")
          case RoomInfoMessage() => context.log.info("RoomInfoMessage")
          case LobbyInfoMessage() => context.log.info("LobbyInfoMessage")
          case GameInfoMessage() => context.log.info("GameInfoMessage")
          case RoomAlreadyExistsMessage() => context.log.info("RoomAlreadyExistsMessage")
          case RoomNotFoundMessage() => context.log.info("RoomNotFoundMessage")
          case GameNotFoundMessage() => context.log.info("GameNotFoundMessage")
        }
        case RegisterSocket(newSocketActor) => {
          context.log.info("registering new socket")
          playerActor(username, newSocketActor)
        }
      }
    }

}