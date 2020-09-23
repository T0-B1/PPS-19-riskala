package org.riskala.controller.routes

import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import org.riskala.controller.auth.LoginJsonSupport._
import org.riskala.controller.auth.{AuthManager, LoginData, RegistrationData}

object RestRoutes {

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
      } ~ entity(as[LoginData]) {
        l => {
          AuthManager.login(l).fold(complete(404, "User not found"))(t=>complete(200, t))
        }
      }
    }
  }

  val registrationPath: server.Route = post {
    path("register") {
      entity(as[RegistrationData]) {
        r => {
          AuthManager.register(r).fold(complete(400, "User already exists"))(t=>complete(200, t))
        }
      }
    }
  }

  def extractTokenHeader: HttpHeader => Option[String] = {
    case HttpHeader("token", value) if AuthManager.checkToken(value) => Some(value)
    case _ => None
  }
}
