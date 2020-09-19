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
  val troopsValue = 3
  val playerState = PlayerState(state, p1, 7)
  val geopolitics: Geopolitics = Set(playerState)


  "A state owner" should {
    "be correctly updated" in {
      assert(geopolitics.updateStateOwner(state, p2)
        .getPlayerState(state).get.owner.equals(p2))
    }
  }

  "The amount of troops in a state" should {
    "be correctly updated" in {
      assertResult(playerState.troops + troopsDelta) {
        geopolitics.modifyStateTroops(state, troopsDelta)
            .getPlayerState(state).get.troops
      }

      assertResult(troopsValue) {
        geopolitics.setStateTroops(state, troopsValue)
          .getPlayerState(state).get.troops
      }
    }
  }

}
