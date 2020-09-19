package org.riskala.model.eventSourcing


import org.junit.runner.RunWith
import org.riskala.model
import org.riskala.model.{Cards, Geopolitics, Player}
import org.riskala.utils.MapLoader
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CommandTest extends AnyWordSpec {

  val p1: Player = Player("p1", "green")
  val p2: Player = Player("p2", "blue")
  val p3: Player = Player("p3", "red")
  val players = Seq(p1, p2, p3)
  val scenario: model.Map = MapLoader.loadMap("italy").get
  val initialSnapshot: GameSnapshot = GameSnapshot.newGame(players, scenario)
  val game = initialSnapshot.copy(nowPlaying = p1, cards = Map.empty + (p1 -> Seq.fill(3)(Cards.Artillery)))
  val attackingState = "Emilia-Romagna"
  val defendingState = "Toscana"
  var geopolitics = game.geopolitics
  geopolitics = Geopolitics.updateStateOwner(attackingState, p1, geopolitics)
  geopolitics = Geopolitics.updateStateOwner(defendingState, p2, geopolitics)

  "No command except EndTurn" should {
    "be feasible" when {
      "is not your turn" in {
        val game = initialSnapshot.copy(nowPlaying = Player("foo", ""))
        assert(!Attack(attackingState, defendingState, 0).feasibility(game).feasible)
        assert(!MoveTroops(attackingState, defendingState, 0).feasibility(game).feasible)
        assert(!Deploy(attackingState,0).feasibility(game).feasible)
        assert(!RedeemBonus(p1, Cards.Artillery).feasibility(game).feasible)
      }
    }
  }

  "End turn" should {
    "only be feasible" when {
      "is your turn" in {
        assert(!EndTurn(p2).feasibility(game).feasible)
        assert(EndTurn(p1).feasibility(game).feasible)
      }
    }
  }

  "An attack" should {
    "not be feasible" when {
      "attacking state does not have enough troops" in {
        assert(!Attack(attackingState, defendingState, Int.MaxValue).feasibility(game).feasible)
      }
    }
    "be feasible" when {
      "having enough troops during own turn" in {
        assert(!Attack(attackingState, defendingState, 1).feasibility(game).feasible)
      }
    }
  }

  "Moving troops" should {
    "not be feasible" when {
      "origin state does not have enough troops" in {
        assert(!MoveTroops(attackingState, defendingState, Int.MaxValue).feasibility(game).feasible)
      }
      "you don't own the destination state" in {
        assert(!MoveTroops(defendingState, attackingState, 1).feasibility(game).feasible)
      }
    }
    "be feasible" when {
      "having enough troops during own turn" in {
        assert(MoveTroops(attackingState, defendingState, 1).feasibility(game).feasible)
      }
    }
  }

  "Deploy troops" should {
    "not be feasible" when {
      "a player does not have enough troops to deploy" in {
        assert(!Deploy(attackingState,Int.MaxValue).feasibility(game).feasible)
      }
      "you don't own the destination state" in {
        assert(!Deploy(defendingState,0).feasibility(game).feasible)
      }
    }
    "be feasible" when {
      "having enough troops during own turn" in {
        assert(Deploy(attackingState,1).feasibility(game).feasible)
      }
    }
  }

  "Redeem bonus" should {
    "not be feasible" when {
      "a player does not have enough cards of the same type" in {
        assert(!RedeemBonus(p1, Cards.Infantry).feasibility(game).feasible)
      }
    }
    "be feasible" when {
      "having enough cards during own turn" in {
        assert(RedeemBonus(p1, Cards.Artillery).feasibility(game).feasible)
      }
    }
  }

}
