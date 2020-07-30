package org.riskala.Model

import org.scalatest.wordspec.AnyWordSpec

class RegionTest extends AnyWordSpec {
  var listState: List[State] = List(StateImpl("Italy"),
    StateImpl("France"),
    StateImpl("Germany"))

  "Region" should {
    "initially be empty" in {
      assert(RegionImpl(List.empty, 2).states.isEmpty)
    }

    "after adding regions, habe size different to 0" in {
      assert(RegionImpl(listState, 2).states.nonEmpty)
    }

    "check if region belongs to state" in {
      assert(RegionImpl(listState, 2).belongs(StateImpl("Italy")))
    }
  }
}
