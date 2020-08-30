package org.riskala.controller

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

object Main extends App {

  implicit val system = ActorSystem("my-system")

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val PORT: Int = System.getProperty("server.port") match {
    case port if Try(port.toInt).isSuccess => port.toInt
    case _ => 8080
  }

  val websocketRoute =
    (get & path("websocket") & parameter("token")) { token =>
      val authSink = sink(token)
      handleWebSocketMessages(Flow.fromSinkAndSource(authSink, source))
    }

  val requestHandler =
    (get & pathPrefix("")){
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromResource("static/index.html")
      } ~ {
        getFromResourceDirectory("static")
      }
    } ~ websocketRoute

  val source: Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)

  def sink(sender: String): Sink[Message, Future[Done]] = Sink.foreach(m => println(s"Received $m from $sender"))

  val staticContentBindingFuture = Http().newServerAt("0.0.0.0", PORT)
    .adaptSettings(_.mapWebsocketSettings(
      _.withPeriodicKeepAliveMode("pong")
       .withPeriodicKeepAliveMaxIdle(1.second)))
    .bindFlow(requestHandler)

  println(s"Server online at port $PORT \n...")

}
