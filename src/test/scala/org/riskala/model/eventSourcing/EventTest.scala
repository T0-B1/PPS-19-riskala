package org.riskala.model.eventSourcing

import org.riskala.model.{Player, PlayerState}
import org.riskala.utils.{MapLoader, Utils}
import org.scalatest.wordspec.AnyWordSpec

class EventTest extends AnyWordSpec {

  val p1 = Player("p1", "green")
  val p2 = Player("p2", "blue")
  val p3 = Player("p3", "red")
  val players = Seq(p1, p2, p3)
  val scenario = MapLoader.loadMap("italy").get
  val initialSnapshot: GameSnapshot = GameSnapshot.newGame(players, scenario)

  "An initialization event" should {
    "not alter the game snapshot" in {
      assert(initialSnapshot.equals(GameInitialized(initialSnapshot).happen(initialSnapshot)))
    }
  }

  "A battle event" when {
    val attackingState = "Emilia-Romagna"
    val defendingState = "Toscana"
    val attackingPlayerState = PlayerState(attackingState, p1, 10)
    val defendingPlayerState = PlayerState(defendingState, p2, 5)
    val playerStates: Set[PlayerState] = initialSnapshot.geopolitics.filterNot(p =>
      p.state.equals(attackingState) || p.state.equals(defendingState)) + attackingPlayerState + defendingPlayerState
    val preBattleGame = initialSnapshot.copy(geopolitics = playerStates)

    "happening" should {
      val attackers = 3
      val passed = 0
      val dead = 0
      val postBattleGame = Battle(attackingState, defendingState, attackers, passed, dead).happen(preBattleGame)
      val atkState = postBattleGame.geopolitics.collectFirst({
        case s if s.state.equals(attackingState) => s
      }).get
      "decrease the number of troops present in the attacking state of an amount equal to the attacking troops" in {
        assert(atkState.troops.equals(attackingPlayerState.troops - attackers))
      }
    }

    "victorious" should {
      val attackers = 5
      val passed = 3
      val dead = 5
      val postBattleGame = Battle(attackingState, defendingState, attackers, passed, dead).happen(preBattleGame)
      val lostState = postBattleGame.geopolitics.collectFirst({
        case s if s.state.equals(defendingState) => s
      }).get
      "result in the attacked state to be conquered" in {
        assert(lostState.owner.equals(attackingPlayerState.owner))
      }
      "result in the attacked state to have an amount of troops equal to the attackingPassed" in {
        assert(lostState.troops.equals(passed))
      }
    }

    "not victorious" should {
      val attackers = 5
      val passed = 0
      val dead = 4
      val postBattleGame = Battle(attackingState, defendingState, attackers, passed, dead).happen(preBattleGame)
      val heldState = postBattleGame.geopolitics.collectFirst({
        case s if s.state.equals(defendingState) => s
      }).get
      "not result in the attacked state to be conquered" in {
        assert(heldState.owner.equals(defendingPlayerState.owner))
      }
      "result in the attacked state to have an amount of troops decreased by an amount equal to defendingCasualties" in {
        assert(heldState.troops.equals(defendingPlayerState.troops - dead))
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

  "A bonus" when {
    "redeemed" should {
      "remove 3 cards from the players hand" in {

      }
      "give the player extra troops to deploy" in {

      }
    }
  }

  "A turn" when {
    "ended" should {
      "update the current player" in {

      }
    }
  }
}
