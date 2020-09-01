package org.riskala.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.model.AttributeKeys.webSocketUpgrade
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.server.Directives._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import org.riskala.controller.routes.RestRoutes._
import org.riskala.controller.routes.WebsocketRoute._
import scala.util.Try

object RouteManager {

  implicit val system = ActorSystem("riskala")

  val PORT: Int = System.getProperty("server.port") match {
    case port if Try(port.toInt).isSuccess => port.toInt
    case _ => 8080
  }

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val staticResourcesHandler: server.Route = concat(staticContent,loginPath,registrationPath,redirectHome)

  val webSocketRequestHandler: server.Route = websocketRoute

  val staticContentBindingFuture = Http().newServerAt("0.0.0.0", PORT)
    .adaptSettings(_.mapWebsocketSettings(
      _.withPeriodicKeepAliveMode("pong")
        .withPeriodicKeepAliveMaxIdle(1.second)))
    .bindFlow(staticResourcesHandler)

  println(s"Server online at port $PORT \n...")

  def exit():Unit = {
    staticContentBindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }

}

