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

  "A battle event" should {
    "decrease the number of troops present in the attacking state of an amount equal to the attacking troops" in {

    }
  }

  "A battle event" when {
    "victorious" should {
      "result in the attacked state to change ownership" in {

      }
      "result in the attacked state to have an amount of troops equal to the attackingPassed" in {

      }
    }
    "not victorious" should {
      "not result in the attacked state to change ownership" in {

      }
      "result in the attacked state to have an amount of troops decreased by an amount equal to defendingCasualties" in {

      }
    }

  }

  "A troops moving event from A to B of n troops" should{
    "decrease the troops in A of n" in {

    }
    "increase the troops in B of n" in {

    }
  }

  "A deploy event" should {
    "increase the troops in the destination state" in {

    }
  }

  "A card drawn event" should {
    "add a card to the correct player" in {
      
    }
  }
}
