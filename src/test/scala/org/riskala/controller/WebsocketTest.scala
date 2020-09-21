package org.riskala.controller

import java.util.Properties

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Flow
import org.junit.runner.RunWith
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.controller.routes.WebsocketRoute
import org.riskala.utils.{TestUtils, Utils}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner
import akka.actor.typed.scaladsl.adapter._

@RunWith(classOf[JUnitRunner])
class WebsocketTest extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val properties: Properties = TestUtils.loadPropertiesFromResources()
  def socketUri(token: String) = s"/websocket?token=$token"

  "A user" should{
    "not be able to open a socket without a valid token" in {
      WS(socketUri(""), Flow.fromFunction(identity)) ~> WebsocketRoute.websocketRoute ~>
        check { response.status shouldEqual StatusCodes.Forbidden }
    }
  }

  "A user" when {
    "logged" should{

      "be able to open a socket using his token" in {
        val token = AuthTest.login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
        WS(socketUri(token), Flow.fromFunction(identity)) ~> WebsocketRoute.websocketRoute ~>
          check {
            response.status shouldEqual StatusCodes.SwitchingProtocols
            isWebSocketUpgrade shouldEqual true
          }
      }

      "trigger the spawn of a playerActor upon opening the socket" in {
        implicit val sys = Server.system
        val token = AuthTest.login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
        WS(socketUri(token), Flow.fromFunction(identity)) ~> WebsocketRoute.websocketRoute ~>
          check {
            Utils.askReceptionistToFind[PlayerMessage](properties.get("testAccountUsername").toString)
              .size shouldEqual 1
          }

      }

    }
  }

}
