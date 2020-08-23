package org.riskala.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Main extends App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val staticResources =
    (get & pathPrefix("")){
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromResource("static/index.html")
      } ~ {
        getFromResourceDirectory("static")
      }
    }

  val bindingFuture = Http().bindAndHandle(staticResources, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
