package org.riskala.Model

import org.scalatest.wordspec.AnyWordSpec
import argonaut.Argonaut._

class SerializationTest extends AnyWordSpec{
  "Bridge" should {
    val bridge = Bridge("Italy","France",false)
    "be serialized to JSON" in {
      assert(bridge.asJson.hasField("state1"))
      assert(bridge.asJson.hasField("state2"))
      assert(bridge.asJson.hasField("userCreated"))
      assert(bridge.asJson.as[Bridge] == bridge)
    }
  }
}
