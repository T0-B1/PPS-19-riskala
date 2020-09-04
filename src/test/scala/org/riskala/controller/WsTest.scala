package org.riskala.controller

import java.util.Properties

import akka.actor.ActorSystem
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.testkit.WSProbe
import akka.http.scaladsl.testkit.WSTestRequestBuilding._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import org.junit.runner.RunWith
import org.riskala.controller.AuthTest.response
import org.riskala.controller.routes.WebsocketRoute
import org.riskala.utils
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.time.SpanSugar._
import akka.stream.scaladsl.{Flow, Sink, Source}
import sun.security.pkcs11.wrapper.Functions
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import akka.testkit

@RunWith(classOf[JUnitRunner])
class WsTest extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val properties: Properties = utils.loadPropertiesFromResources()
  def socketUri(token: String) = s"/websocket?token=$token"

  "A user" should{
    "not be able to open a socket without a valid token" in {
      WS(socketUri(""), Flow.fromFunction(identity)) ~> WebsocketRoute.websocketRoute ~>
        check { response.status shouldEqual StatusCodes.Forbidden }
    }
  }

  "A user" when {
    "logged" should{

      "be able to open a socket using this token" in {
        val token = AuthTest.login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
        WS(socketUri(token), Flow.fromFunction(identity)) ~> WebsocketRoute.websocketRoute ~>
          check { response.status shouldEqual StatusCodes.SwitchingProtocols }
      }

      "trigger the spawn of a playerActor upon opening the socket" in {
        val token = AuthTest.login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
        WS(socketUri(token), Flow.fromFunction(identity)) ~> WebsocketRoute.websocketRoute ~>
          check { response.status shouldEqual StatusCodes.SwitchingProtocols }
      }

      "be able to send messages to the playerActor" in {
      }

      "be able to receive messages from the playerActor" in {
      }

      "cause the death of the playerActor upon disconnecting" in {
      }

    }
  }

}
