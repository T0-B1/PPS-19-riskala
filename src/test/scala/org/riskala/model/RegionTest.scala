package org.riskala.model

import org.junit.runner.RunWith
import org.riskala.model.State.State
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RegionTest extends AnyWordSpec {
  var listState: List[State] = List("Italy","France","Germany")

  "Region" should {
    "initially be empty" in {
      assert(Region(List.empty, 2).states.isEmpty)
    }

    "after adding regions, have size different to 0" in {
      assert(Region(listState, 2).states.nonEmpty)
    }

    "check if region belongs to state" in {
      assert(Region(listState, 2).hasState("Italy"))
    }
  }
}
