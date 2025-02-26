package org.riskala.controller

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.riskala.controller.routes.RestRoutes._
import org.riskala.controller.routes.WebsocketRoute._
import org.riskala.controller.actors.Messages.LobbyMessage
import org.riskala.controller.actors.lobby.LobbyManager
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.util.Try

object Server {

  implicit val system: ActorSystem[LobbyMessage] = ActorSystem(LobbyManager(), "riskala")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  private var serverBindingFuture: Option[Future[Http.ServerBinding]] = None
  val routing: Route = concat(staticContent, loginPath, registrationPath, websocketRoute, redirectHome)
  private val PORT: Int = System.getProperty("server.port") match {
    case port if Try(port.toInt).isSuccess => port.toInt
    case _ => 8080
  }

  def start(): Unit = {
    serverBindingFuture = Some(Http().newServerAt("0.0.0.0", PORT)
      .adaptSettings(_.mapWebsocketSettings(
        _.withPeriodicKeepAliveMode("pong")
          .withPeriodicKeepAliveMaxIdle(1.second)))
      .bindFlow(routing))
    println(s"Server online at port $PORT \n...")
  }
  
  def exit():Unit = {
    serverBindingFuture match {
      case Some(sbf) => sbf
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }

}

