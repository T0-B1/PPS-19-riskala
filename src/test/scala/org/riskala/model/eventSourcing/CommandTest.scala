package org.riskala.model.eventSourcing


import org.junit.runner.RunWith
import org.riskala.model
import org.riskala.model.{Cards, Player}
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
  val game = initialSnapshot.copy(nowPlaying = p1)
  val attackingState = "Emilia-Romagna"
  val defendingState = "Toscana"

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

      }
    }
    "be feasible" when {
      "having enough troops during own turn" in {

      }
    }
  }

  "Moving troops" should {
    "not be feasible" when {
      "origin state does not have enough troops" in {

      }
      "you don't own the destination state" in {

      }
    }
    "be feasible" when {
      "having enough troops during own turn" in {

      }
    }
  }

  "Deploy troops" should {
    "not be feasible" when {
      "a player does not have enough troops to deploy" in {

      }
      "you don't own the destination state" in {

      }
    }
    "be feasible" when {
      "having enough troops during own turn" in {

      }
    }
  }

  "Redeem bonus" should {
    "not be feasible" when {
      "a player does not have enough cards of the same type" in {

      }
    }
    "be feasible" when {
      "having enough cards during own turn" in {

      }
    }
  }

}
