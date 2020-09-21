package org.riskala.controller.routes

import akka.actor.typed.ActorRef
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.{Done, NotUsed, actor}
import org.riskala.controller.actors.{PlayerActor, PlayerLobbyBehavior, PlayerMessages}
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.controller.{AuthManager, Server}
import org.riskala.utils.Utils

import scala.concurrent.Future

object WebsocketRoute {

  import Server.system

  val websocketRoute: Route =
    (get & path("websocket") & parameter("token")) { token =>
      if( (!AuthManager.checkToken(token)) || AuthManager.getUserName(token).isEmpty) complete(StatusCodes.Forbidden)
      else{
        val username = AuthManager.getUserName(token).get
        handleWebSocketMessages(createSocketFlow(username))
      }
    }

  private def createSocketFlow(username: String): Flow[Message, Message, NotUsed] = {
    val (wsActor, wsSource) = untypedActorSource().preMaterialize()
    val playerActorRef: ActorRef[PlayerMessage] = spawnOrGetPlayer(username, wsActor)
    Flow.fromSinkAndSource(sinkFromActor(playerActorRef), wsSource)
  }

  private def spawnOrGetPlayer(username: String, newSocket: actor.ActorRef): ActorRef[PlayerMessage] = {
    Utils.askReceptionistToFind[PlayerMessage](username).toSeq match {
      // No PlayerActor registered found
      case Seq() => {
        // TODO use spawn protocol
        // https://doc.akka.io/docs/akka/current/typed/actor-lifecycle.html#spawnprotocol
        val ref: ActorRef[PlayerMessage] = system.systemActorOf(PlayerActor(username, newSocket), username)
        system.receptionist ! Receptionist.Register(ServiceKey[PlayerMessage](username), ref)
        ref
      }
      // The first PlayerActor found
      case Seq(first, rest@_*) => {
        first ! PlayerMessages.RegisterSocket(newSocket)
        first
      }
    }
  }

  private def typedActorSource(): Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.dropHead)

  private def untypedActorSource(): Source[Message, actor.ActorRef] =
    Source.actorRef[Message](bufferSize = 8, overflowStrategy = OverflowStrategy.dropHead)

  private def sinkFromActor(playerRef: ActorRef[PlayerMessage]): Sink[Message, Future[Done]] =
    Sink.foreach(m => playerRef ! PlayerMessages.SocketMessage(m.asTextMessage.getStrictText))

}
