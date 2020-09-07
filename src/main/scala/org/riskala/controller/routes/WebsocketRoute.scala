package org.riskala.controller.routes

import akka.actor.typed.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.{Done, NotUsed, actor}
import org.riskala.controller.actors.PlayerActor
import org.riskala.controller.actors.ServerMessages.{PlayerMessage, SocketMessage}
import org.riskala.controller.{AuthManager, Server}

import scala.concurrent.Future

object WebsocketRoute {

  import Server.system

  val websocketRoute: Route =
    (get & path("websocket") & parameter("token")) { token =>
      if( (!AuthManager.checkToken(token)) || AuthManager.getUser(token).isEmpty) complete(StatusCodes.Forbidden)
      else{
        val username = AuthManager.getUser(token).get
        handleWebSocketMessages(createSocketFlow(username))
      }
    }

  private def createSocketFlow(username: String): Flow[Message, Message, NotUsed] = {
    val (wsActor, wsSource) = untypedActorSource().preMaterialize()
    // TODO use spawn protocol
    // https://doc.akka.io/docs/akka/current/typed/actor-lifecycle.html#spawnprotocol
    val playerActorRef: ActorRef[PlayerMessage] = system.systemActorOf(PlayerActor(username, wsActor), username)
    Flow.fromSinkAndSource(sinkFromActor(playerActorRef), wsSource)
  }

  private def typedActorSource(): Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.dropHead)

  private def untypedActorSource(): Source[Message, actor.ActorRef] =
    Source.actorRef[Message](bufferSize = 8, overflowStrategy = OverflowStrategy.dropHead)

  private def sinkFromActor(playerRef: ActorRef[PlayerMessage]): Sink[Message, Future[Done]] =
    Sink.foreach(m => playerRef ! SocketMessage(m.asTextMessage.getStrictText))

}
