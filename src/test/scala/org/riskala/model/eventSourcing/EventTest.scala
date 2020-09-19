package org.riskala.model.eventSourcing

import org.riskala.model.Player
import org.riskala.utils.{MapLoader, Utils}
import org.scalatest.wordspec.AnyWordSpec

class EventTest extends AnyWordSpec {

  val players = Seq(Player("p1", "green"), Player("p2", "blue"), Player("p3", "red"))
  val scenario = MapLoader.loadMap("italy").get
  val initialSnapshot: GameSnapshot = GameSnapshot.newGame(players, scenario)

  "An initialization event" should {
    "not alter the game snapshot" in {
      assert(initialSnapshot.equals(GameInitialized(initialSnapshot).happen(initialSnapshot)))
    }
  }

}
