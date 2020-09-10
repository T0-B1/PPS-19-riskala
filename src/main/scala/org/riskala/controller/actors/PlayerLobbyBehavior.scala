package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages.LobbyMessage
import org.riskala.model.lobby.LobbyMessages.JoinTo
import org.riskala.utils.Parser

object PlayerLobbyBehavior {

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

          val referent: ActorRef[LobbyMessage] = ???
          val typed = Parser.unwrap(payload)
          typed.classType match {
            case _: Class[JoinMessage] => {
              Parser.retrieveMessage(typed.payload, JoinMessage.JoinCodecJson.Decoder)
                .foreach(j => referent ! JoinTo(context.self,j.name))
            }
          }

          Parser.retrieveWrapped(payload).foreach(wrapped=>wrapped.classType match {
            case "ErrorMessage" =>
              val opt = Parser.retrieveMessage[ErrorMessage](wrapped.payload,ErrorMessage.ErrorCodecJson.Decoder)

          })
          nextBehavior()
        }
        case RoomInfoMessage(roomInfo) => context.log.info("RoomInfoMessage"); nextBehavior()
        case LobbyInfoMessage(lobbyInfo) => context.log.info("LobbyInfoMessage"); nextBehavior()
        case GameInfoMessage() => context.log.info("GameInfoMessage"); nextBehavior()
        case JoinMessage(name) => context.log.info("JoinMessage"); nextBehavior()
        case ReadyMessage() => context.log.info("ReadyMessage"); nextBehavior()
        case CreateMessage(name,maxPlayer,scenario) => context.log.info("CreateMessage"); nextBehavior()
        case LeaveMessage() => context.log.info("LeaveMessage"); nextBehavior()
        case ActionMessage(from,to,attacking,defending,invading) => context.log.info("ActionMessage"); nextBehavior()
        case RedeemBonusMessage(cardType) => context.log.info("RedeemBonusMessage"); nextBehavior()
        case EndTurnMessage() => context.log.info("EndTurnMessage"); nextBehavior()
        case ErrorMessage(error) => context.log.info("ErrorMessage"); nextBehavior()
        case RegisterSocket(newSocketActor) => {
          context.log.info("registering new socket")
          nextBehavior(newSocket = newSocketActor)
        }
      }
    }

}