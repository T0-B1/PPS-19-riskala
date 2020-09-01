package org.riskala.controller.routes

import akka.Done
import akka.actor.typed.ActorRef
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource

import scala.concurrent.Future

object WebocketRoute {

  val websocketRoute: Route =
    (get & path("websocket") & parameter("token")) { token =>
      val authSink = sink(token)
      handleWebSocketMessages(Flow.fromSinkAndSource(authSink, source))
    }

  private val source: Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)

  private def sink(sender: String): Sink[Message, Future[Done]] = Sink.foreach(m => println(s"Received $m from $sender"))

}
