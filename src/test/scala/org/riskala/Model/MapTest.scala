package org.riskala.Model

import org.scalatest.wordspec.AnyWordSpec

class MapTest extends AnyWordSpec {
  val ita = StateImpl("Italy")
  val fra = StateImpl("France")
  val swi = StateImpl("Switzerland")
  val ger = StateImpl("Germany")
  val spa = StateImpl("Spain")
  val states = List(ita, fra, swi, ger, spa)
  val bridges = List(BridgeImpl(ita,fra,false),
    BridgeImpl(ita,swi,false),
    BridgeImpl(swi,fra,false),
    BridgeImpl(fra,spa,false),
    BridgeImpl(fra,ger,false))
  val map = MapImpl("Europe",List.empty,states,bridges)

  "Map neighbors" should {
    "give a list of neighbors" in {
      assert(map.neighbor(ita).contains(fra))
      assert(map.neighbor(ita).contains(swi))
      assert(map.neighbor(spa).contains(fra))
    }

    "check if two states are neighbors" in {
      assert(map.areNeighbor(ita, fra))
      assert(map.areNeighbor(fra, ita))
      //assert(map.areNeighbor(ita, ita))
    }
  }
}
