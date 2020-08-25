package org.riskala.Model

import org.riskala.Model.State.State
import org.scalatest.wordspec.AnyWordSpec

class RegionTest extends AnyWordSpec {
  var listState: List[State] = List("Italy","France","Germany")

  "Region" should {
    "initially be empty" in {
      assert(Region(List.empty, 2).states.isEmpty)
    }

    "after adding regions, habe size different to 0" in {
      assert(Region(listState, 2).states.nonEmpty)
    }

    "check if region belongs to state" in {
      assert(Region(listState, 2).hasState("Italy"))
    }
  }
}