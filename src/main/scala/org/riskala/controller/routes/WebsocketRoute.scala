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
import org.riskala.controller.actors.ServerMessages.PlayerMessage

import scala.concurrent.Future

object WebsocketRoute {

  import Server.system

  val websocketRoute: Route =
    (get & path("websocket") & parameter("token")) { token =>
      if(!AuthManager.checkToken(token)) complete(StatusCodes.Forbidden)
      else{
        val authSink = sink(token)

        val (wsActor, wsSource) =  Source.actorRef[Message](
          completionMatcher = {
            case Done =>
              // complete stream immediately if we send it Done
              CompletionStrategy.immediately
          },
          // never fail the stream because of a message
          failureMatcher = PartialFunction.empty,
          bufferSize = 100,
          overflowStrategy = OverflowStrategy.dropHead).preMaterialize()

        handleWebSocketMessages(Flow.fromSinkAndSource(authSink, source))
      }
    }

  private val source: Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)

  private def sink(sender: String): Sink[Message, Future[Done]] = Sink.foreach(m => println(s"Received $m from $sender"))

}
