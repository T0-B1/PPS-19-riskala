package org.riskala.model

import org.scalatest.wordspec.AnyWordSpec
import argonaut.Argonaut._
import org.junit.runner.RunWith
import org.riskala.model.State.State
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SerializationTest extends AnyWordSpec{
  val ita: State = "Italy"
  val fra: State = "France"
  val swi: State = "Switzerland"
  val ger: State = "Germany"
  val spa: State = "Spain"
  val states = Set(ita, fra, swi, ger, spa)
  val regions = Set(Region(Set(ita,swi),4),Region(Set(spa,ger,fra),6))
  val bridges = Set(Bridge(ita,fra,false),
    Bridge(ita,swi,false),
    Bridge(swi,fra,false),
    Bridge(fra,spa,false),
    Bridge(fra,ger,false))
  val map = MapImpl("Europe",regions,states,bridges)
  "Bridge" should {
    val bridge = bridges.head
    "be serialized to JSON" in {
      assert(bridge.asJson.hasField("state1"))
      assert(bridge.asJson.hasField("state2"))
      assert(bridge.asJson.hasField("userCreated"))
      assert(bridge.asJson.as[Bridge].toOption.get == bridge)
    }
  }

  "Region" should {
    val region = regions.head
    "be serialized to JSON" in {
      assert(region.asJson.hasField("bonus"))
      assert(region.asJson.hasField("states"))
      assert(region.asJson.field("bonus").get.as[Int].toOption.get == region.bonus)
      assert(region.asJson.field("states").get.as[List[State]].toOption.get.contains(ita))
      assert(region.asJson.as[Region].toOption.get == region)
    }
  }

  "Map" should {
    "be serialized to JSON" in {
      assert(map.asJson.hasField("name"))
      assert(map.asJson.hasField("states"))
      assert(map.asJson.hasField("regions"))
      assert(map.asJson.hasField("bridges"))
      assert(map.asJson.as[MapImpl].toOption.get == map)
      assert(map.asJson.field("regions").get.as[List[Region]].toOption.get.contains(regions.head))
    }
  }

  "Player" should {
    val player = Player("p1","red")
    "be serialized to JSON" in {
      assert(player.asJson.hasField("nickname"))
      assert(player.asJson.hasField("color"))
      assert(player.asJson.as[Player].toOption.get == player)
    }
  }

  "PlayerState" should {
    val pState = PlayerState(Player("p1","red"),2)
    "be serialized to JSON" in {
      assert(pState.asJson.hasField("owner"))
      assert(pState.asJson.hasField("troops"))
      assert(pState.asJson.as[PlayerState].toOption.get == pState)
    }
  }
}
