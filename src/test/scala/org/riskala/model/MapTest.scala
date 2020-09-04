package org.riskala.model

import org.junit.runner.RunWith
import org.riskala.model.State.State
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MapTest extends AnyWordSpec {
  val ita: State = "Italy"
  val fra: State = "France"
  val swi: State = "Switzerland"
  val ger: State = "Germany"
  val spa: State = "Spain"
  val states = List(ita, fra, swi, ger, spa)
  val bridges = List(Bridge(ita,fra,false),
    Bridge(ita,swi,false),
    Bridge(swi,fra,false),
    Bridge(fra,spa,false),
    Bridge(fra,ger,false))
  val map = MapImpl("Europe",List.empty,states,bridges)

  "Map neighbors" should {
    "give a list of neighbors" in {
      assert(map.getNeighbors(ita).contains(fra))
      assert(map.getNeighbors(ita).contains(swi))
      assert(map.getNeighbors(spa).contains(fra))
    }

    "check if two states are neighbors" in {
      assert(map.areNeighbor(ita, fra))
      assert(map.areNeighbor(fra, ita))
      //assert(map.areNeighbor(ita, ita))
    }
  }
}