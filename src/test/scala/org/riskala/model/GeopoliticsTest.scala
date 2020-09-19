package org.riskala.model

import org.junit.runner.RunWith
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GeopoliticsTest extends AnyWordSpec {

  val state = "foo"
  val p1 = Player("p1", "")
  val p2 = Player("p2", "")
  val troopsDelta = -5
  val playerState = PlayerState(state, p1, 7)
  val geopolitics = Set(playerState)

  "A state owner" should {
    "be correctly updated" in {
      assert(Geopolitics.getPlayerState(state,
        Geopolitics.updateStateOwner(state, p2, geopolitics)).get
        .owner.equals(p2))
    }
  }

  "The amount of troops in a state" should {
    "be correctly updated" in {
      assertResult(playerState.troops + troopsDelta) {
        Geopolitics.getPlayerState(state,
          Geopolitics.modifyStateTroops(state, troopsDelta, geopolitics)).get.troops
      }
    }
  }

}
