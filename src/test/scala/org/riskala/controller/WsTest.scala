package org.riskala.controller

import java.util.Properties

import org.junit.runner.RunWith
import org.riskala.utils
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WsTest extends AnyWordSpec with Matchers {

  val properties: Properties = utils.loadPropertiesFromResources()

  "A user" when {
    "logged" should{

      "be able to open a socket using this token" in {

      }

      "trigger the spawn of a playerActor upon opening the socket" in {

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
