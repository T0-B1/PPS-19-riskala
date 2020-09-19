package org.riskala.model.eventSourcing


import org.junit.runner.RunWith
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CommandTest extends AnyWordSpec {

  "No command except EndTurn" should {
    "be feasible" when {
      "is not your turn" in {

      }
    }
  }

  "End turn" should {
    "only be feasible" when {
      "is your turn" in {

      }
    }
  }

  "An attack" should {
    "not be feasible" when {
      "attacking state does not have enough troops" in {

      }
    }
  }

  "Moving troops" should {
    "not be feasible" when {
      "origin state does not have enough troops" in {

      }
    }
  }

  "Deploy troops" should {
    "not be feasible" when {
      "a player does not have enough troops to deploy" in {

      }
    }
  }

  "Redeem bonus" should {
    "not be feasible" when {
      "a player does not have enough cards of the same type" in {

      }
    }
  }

}
