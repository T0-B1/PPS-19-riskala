package org.riskala.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import org.riskala.controller.routes.RestRoutes._
import org.riskala.controller.routes.WebsocketRoute._

import scala.util.Try

object RouteManager {

  implicit val system = ActorSystem("riskala")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val PORT: Int = System.getProperty("server.port") match {
    case port if Try(port.toInt).isSuccess => port.toInt
    case _ => 8080
  }

  val allRoutes: Route = concat(staticContent,loginPath,registrationPath,redirectHome, websocketRoute)

  val serverBindingFuture: Future[Http.ServerBinding] = Http().newServerAt("0.0.0.0", PORT)
    .adaptSettings(_.mapWebsocketSettings(
      _.withPeriodicKeepAliveMode("pong")
        .withPeriodicKeepAliveMaxIdle(1.second)))
    .bindFlow(allRoutes)

  println(s"Server online at port $PORT \n...")

  def exit():Unit = {
    serverBindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }

}

