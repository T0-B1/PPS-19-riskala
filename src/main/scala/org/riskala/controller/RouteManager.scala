package org.riskala.controller

import akka.Done
import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.AttributeKeys.webSocketUpgrade
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource

import scala.concurrent.Future
import scala.concurrent.duration._
import spray.json.DefaultJsonProtocol


case class Login(username: String, password: String)
object LoginJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val LoginFormats = jsonFormat2(Login)
}

object RouteManager {

  implicit val system = ActorSystem("my-system")

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  import LoginJsonSupport._

  val staticResourcesHandler = concat(
    (get & pathPrefix("")){
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromResource("static/index.html")
      } ~ {
        getFromResourceDirectory("static")
      }
    }, post {
      path("login") {
        entity(as[Login]){
          l => complete(s"Tutto ok. User:${l.username}, psw:${l.password}")
        }
      }
    })

  val webSocketRequestHandler: HttpRequest => HttpResponse = {

    case req @ HttpRequest(GET, Uri.Path("/websocket"), _, _, _) =>
      req.attribute(webSocketUpgrade) match {
        case Some(upgrade) => req.uri.query().get("token") match {
          case Some(token) => {
            //upgrade.handleMessages(webSocketHandler(token))
            val (sourceActor, newSource) = source.preMaterialize()
            upgrade.handleMessagesWithSinkSource(sink(token), newSource)
          }
          case None => HttpResponse(400, entity = "Missing token!")
        }
        case None => HttpResponse(400, entity = "Not a valid websocket request!")
      }
    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
  }

  val source: Source[Message, ActorRef[Message]] = ActorSource.actorRef[Message](completionMatcher = {
    case _ => CompletionStrategy.immediately
  }, PartialFunction.empty, bufferSize = 8, overflowStrategy = OverflowStrategy.fail)

  def sink(sender: String): Sink[Message, Future[Done]] = Sink.foreach(m => println(s"Received $m from $sender"))

  def webSocketHandler(token: String)  =
    Flow[Message]
      .mapConcat {
        // we match but don't actually consume the text message here,
        // rather we simply stream it back as the tail of the response
        // this means we might start sending the response even before the
        // end of the incoming message has been received
        case tm: TextMessage => {

          tm.textStream.runWith(Sink.fold[String, String]("")(_ + _))
            .onComplete(s => println(s"Received: $s from $token"))
          TextMessage(Source.single("Hello ") ++ tm.textStream) :: Nil
        }
        case bm: BinaryMessage =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }

  val staticContentBindingFuture = Http().newServerAt("localhost", 8080).bindFlow(staticResourcesHandler)
  val websocketBindingFuture = Http().newServerAt("localhost", 8081)
    .adaptSettings(_.mapWebsocketSettings(
      _.withPeriodicKeepAliveMode("pong")
        .withPeriodicKeepAliveMaxIdle(1.second)))
    .bindSync(webSocketRequestHandler)

  def exit():Unit = {
    staticContentBindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
    websocketBindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
