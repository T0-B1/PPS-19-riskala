package org.riskala.controller.routes

import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import org.riskala.controller.{AuthManager, Login, Register}
import org.riskala.controller.LoginJsonSupport._

object RestRoutes {
  //TODO add comment

  val staticContent: server.Route = (get & pathPrefix("")){
    (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
      getFromResource("static/index.html")
    } ~ {
      getFromResourceDirectory("static")
    }
  }

  val redirectHome: server.Route = get {
    getFromResource("static/index.html")
  }

  val loginPath: server.Route = post {
    path("login") {
      headerValue(extractTokenHeader) {
        token => complete(200,token)
      } ~ entity(as[Login]) {
        l => {
          val optToken = AuthManager.login(l)
          if (optToken.nonEmpty)
            complete(200, optToken.get)
          else
            complete(404, "User not found")
        }
      }
    }
  }

  val registrationPath: server.Route = post {
    path("register") {
      entity(as[Register]) {
        r => {
          val optToken = AuthManager.register(r)
          if (optToken.nonEmpty)
            complete(200, optToken.get)
          else
            complete(400, "User already exists")
        }
      }
    }
  }

  def extractTokenHeader: HttpHeader => Option[String] = {
    case HttpHeader("token", value) if AuthManager.checkToken(value) => Some(value)
    case _ => None
  }
}
