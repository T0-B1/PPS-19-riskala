package org.riskala.Model

import org.scalatest.wordspec.AnyWordSpec
import argonaut.Argonaut._
import org.riskala.Model.State.State

class SerializationTest extends AnyWordSpec{
  "Bridge" should {
    val bridge = Bridge("Italy","France",false)
    "be serialized to JSON" in {
      assert(bridge.asJson.hasField("state1"))
      assert(bridge.asJson.hasField("state2"))
      assert(bridge.asJson.hasField("userCreated"))
      assert(bridge.asJson.as[Bridge].toOption.get == bridge)
    }
  }

  "Region" should {
    val region = Region(List("Italy","France","Spain"),5)
    "be serialized to JSON" in {
      assert(region.asJson.hasField("bonus"))
      assert(region.asJson.hasField("states"))
      assert(region.asJson.field("bonus").get.as[Int].toOption.get == 5)
      assert(region.asJson.field("states").get.as[List[State]].toOption.get.contains("Spain"))
      assert(region.asJson.as[Region].toOption.get == region)
    }
  }
}
