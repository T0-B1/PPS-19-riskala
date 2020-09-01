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
import scala.io.StdIn

object Main extends App {

  val route = RouteManager

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



  println(s"Server online at http://localhost:8080/\nPress enter to stop...")
  StdIn.readLine()
  route.exit()

}
