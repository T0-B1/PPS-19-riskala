package org.riskala.controller.routes

import akka.Done
import akka.actor.typed.{ActorRef, Props}
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import org.riskala.controller.AuthManager
import org.riskala.controller.Server
import org.riskala.controller.actors.PlayerActor
import org.riskala.controller.actors.ServerMessages.{PlayerMessage, ServerMessage, SocketMessage}
import org.riskala.view.SocketActor.SocketMessage

import scala.concurrent.Future

object WebsocketRoute {

  import Server.system

  val websocketRoute: Route =
    (get & path("websocket") & parameter("token")) { token =>
      if( (!AuthManager.checkToken(token)) || AuthManager.getUser(token).isEmpty) complete(StatusCodes.Forbidden)
      else{
        val username = AuthManager.getUser(token).get
        system.log.info(s"Websocket created for player $username")
        val (wsActor, wsSource) = source.preMaterialize()
        val playerActorRef: ActorRef[PlayerMessage] = system.systemActorOf(PlayerActor(username), username)
        val wsSink = sink(playerActorRef)
        // TODO use spawn protocol
        // https://doc.akka.io/docs/akka/current/typed/actor-lifecycle.html#spawnprotocol
        handleWebSocketMessages(Flow.fromSinkAndSource(wsSink, wsSource))
      }
    }

  private val source: Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.dropHead)

  private def sink(playerRef: ActorRef[PlayerMessage]): Sink[Message, Future[Done]] =
    Sink.foreach(m => playerRef ! SocketMessage(m.asTextMessage.getStrictText))

}
