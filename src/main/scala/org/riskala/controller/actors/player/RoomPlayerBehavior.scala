package org.riskala.controller.actors.player

import akka.actor
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.model.ws.TextMessage
import org.riskala.controller.actors.player.PlayerMessages._
import org.riskala.controller.actors.Messages.{Logout, RoomMessage}
import org.riskala.model.Player
import org.riskala.controller.actors.room.RoomMessages.{Join, Leave, Ready, UnReady}
import org.riskala.utils.Parser
import org.riskala.client.messages.FromClientMessages.ReadyMessage
import org.riskala.client.messages.ToClientMessages
import org.riskala.client.messages.ToClientMessages.RoomInfo

object RoomPlayerBehavior {

  /**
   * Creates a PlayerRoomBehavior which handles game messages
   * @param username Username of the player
   * @param room The actorRef of the roomManager
   * @param socket Classic Akka Actor which handles a socket
   * @return A new PlayerRoomBehavior
   * */
  def apply(username: String, room: ActorRef[RoomMessage], socket: actor.ActorRef): Behavior[PlayerMessage] = {
    roomPlayerBehavior(username, room, socket)
  }

  private def roomPlayerBehavior(username: String,
                                 room: ActorRef[RoomMessage],
                                 socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.receive { (context, message) =>

    message match {

      case SocketMessage(payload) =>
        context.log.info(s"PlayerActor of $username received socket payload: $payload")
        val wrappedOpt = Parser.retrieveWrapped(payload)
        if(wrappedOpt.isDefined) {
          val wrapped = wrappedOpt.get
          wrapped.classType match {

            case "ReadyMessage" =>
              context.log.info("PlayerRoomActor received ReadyMessage")
              Parser.retrieveMessage(wrapped.payload, ReadyMessage.ReadyMessageCodecJson.Decoder)
                .foreach(j => room ! Ready(Player(username,j.color), context.self))
              Behaviors.same

            case "LeaveMessage" =>
              context.log.info("PlayerRoomActor received LeaveMessage")
              room ! Leave(context.self)
              Behaviors.same

            case "LogoutMessage" =>
              context.log.info("PlayerRoomActor received LogoutMessage")
              room ! Logout(context.self)
              Behaviors.stopped

            case "UnReadyMessage" =>
              context.log.info("PlayerRoomActor received UnReadyMessage")
              room ! UnReady(username, context.self)
              Behaviors.same

            case _ =>
              context.log.info("PlayerRoomActor received an unhandled message, IGNORED")
              Behaviors.same

          }
        } else {
          context.log.info("PlayerLobbyActor failed to retrieve message, IGNORED")
          Behaviors.same
        }

      case RoomInfoMessage(roomInfo) =>
        context.log.info(s"PlayerActor of $username received RoomInfoMessage")
        socket ! TextMessage(Parser.wrap("RoomInfo",roomInfo,RoomInfo.RoomInfoCodecJson.Encoder))
        Behaviors.same

      case LobbyReferent(lobby) =>
        context.log.info(s"PlayerActor of $username received LobbyReferent")
        LobbyPlayerBehavior(username,lobby,socket)

      case GameReferent(game) =>
        context.log.info(s"PlayerActor of $username received GameReferent")
        GamePlayerBehavior(username,game,socket)

      case RegisterSocket(newSocketActor) =>
        context.log.info(s"PlayerActor of $username registering new socket")
        room ! Leave(context.self)
        room ! Join(context.self)
        roomPlayerBehavior(username, room, newSocketActor)

      case errorMessage: PlayerMessages.ErrorMessage =>
        context.log.info(s"PlayerActor of $username received ErrorMessage")
        val clientError = ToClientMessages.ErrorMessage(errorMessage.error)
        socket ! TextMessage(Parser.wrap("ErrorMessage",
          clientError,
          ToClientMessages.ErrorMessage.ErrorCodecJson.Encoder))
        Behaviors.same

      case other =>
        context.log.info(s"PlayerActor of $username received "+ other +", IGNORED")
        Behaviors.same
    }
  }
}
