package org.riskala.model.map

import org.junit.runner.RunWith
import org.riskala.model.Player
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GeopoliticsTest extends AnyWordSpec {

  val state = "foo"
  val p1: Player = Player("p1", "")
  val p2: Player = Player("p2", "")
  val troopsDelta: Int = -5
  val troopsValue = 3
  val playerState: PlayerState = PlayerState(state, p1, 7)
  val geopolitics: Geopolitics = Set(playerState)


  "A state owner" should {
    "be correctly updated" in {
      assert(geopolitics.updateStateOwner(state, p2)
        .getPlayerStateByName(state).get.owner.equals(p2))
    }
  }

  "The amount of troops in a state" should {
    "be correctly updated" in {
      assertResult(playerState.troops + troopsDelta) {
        geopolitics.modifyStateTroops(state, troopsDelta)
            .getPlayerStateByName(state).get.troops
      }

      assertResult(troopsValue) {
        geopolitics.setStateTroops(state, troopsValue)
          .getPlayerStateByName(state).get.troops
      }
    }
  }

}
