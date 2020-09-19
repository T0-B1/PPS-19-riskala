package org.riskala.model.eventSourcing

import org.junit.runner.RunWith
import org.riskala.model
import org.riskala.model.Cards.Cards
import org.riskala.model.State.State
import org.riskala.model.{Cards, Geopolitics, Player, PlayerState}
import org.riskala.utils.MapLoader
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EventTest extends AnyWordSpec {

  val p1: Player = Player("p1", "green")
  val p2: Player = Player("p2", "blue")
  val p3: Player = Player("p3", "red")
  val players = Seq(p1, p2, p3)
  val scenario: model.Map = MapLoader.loadMap("italy").get
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
    val playerStates: Geopolitics = initialSnapshot.geopolitics.filterNot(p =>
      p.state.equals(attackingState) || p.state.equals(defendingState)) + attackingPlayerState + defendingPlayerState
    val preBattleGame = initialSnapshot.copy(geopolitics = playerStates)

    "happening" should {
      val attackers = 3
      val passed = 0
      val dead = 0
      val postBattleGame = Battle(attackingState, defendingState, attackers, passed, dead).happen(preBattleGame)
      val atkState = getPlayerStateByName(attackingState, postBattleGame)
      "decrease the number of troops present in the attacking state of an amount equal to the attacking troops" in {
        assertResult(atkState.troops) {
          attackingPlayerState.troops - attackers
        }
      }
    }

    "victorious" should {
      val attackers = 5
      val passed = 3
      val dead = 5
      val postBattleGame = Battle(attackingState, defendingState, attackers, passed, dead).happen(preBattleGame)
      val lostState = getPlayerStateByName(defendingState, postBattleGame)
        "result in the attacked state to be conquered" in {
        assert(lostState.owner.equals(attackingPlayerState.owner))
      }
      "result in the attacked state to have an amount of troops equal to the attackingPassed" in {
        assertResult(lostState.troops) {
          passed
        }
      }
    }

    "not victorious" should {
      val attackers = 5
      val passed = 0
      val dead = 4
      val postBattleGame = Battle(attackingState, defendingState, attackers, passed, dead).happen(preBattleGame)
      val heldState = getPlayerStateByName(defendingState, postBattleGame)
      "not result in the attacked state to be conquered" in {
        assert(heldState.owner.equals(defendingPlayerState.owner))
      }
      "result in the attacked state to have an amount of troops decreased by an amount equal to defendingCasualties" in {
        assertResult(heldState.troops) {
          defendingPlayerState.troops - dead
        }
      }
    }
  }

  "A troops moving event from A to B of n troops" should{
    val fromState = "Emilia-Romagna"
    val toState = "Toscana"
    val movedTroops = 3
    val fromPlayerState = PlayerState(fromState, p1, 5)
    val toPlayerState = PlayerState(toState, p1, 5)
    val playerStates: Geopolitics = initialSnapshot.geopolitics.filterNot(p =>
      p.state.equals(fromState) || p.state.equals(toState)) + fromPlayerState + toPlayerState
    val preMoveGame = initialSnapshot.copy(geopolitics = playerStates)
    val postMoveGame = TroopsMoved(fromState, toState, movedTroops).happen(preMoveGame)
    val A = getPlayerStateByName(fromState, postMoveGame)
    val B = getPlayerStateByName(toState, postMoveGame)

    "decrease the troops in A of n" in {
      assertResult(A.troops) {
        fromPlayerState.troops - movedTroops
      }
    }
    "increase the troops in B of n" in {
      assertResult(B.troops) {
        toPlayerState.troops + movedTroops
      }
    }
  }

  "A deploy event" should {
    val toState = "Emilia-Romagna"
    val deployed = 3
    val toPlayerState = PlayerState(toState, p1, 5)
    val playerStates: Geopolitics = initialSnapshot.geopolitics.filterNot(p =>
      p.state.equals(toState)) + toPlayerState
    val preDeployGame = initialSnapshot.copy(geopolitics = playerStates)
    val postDeployGame = TroopsDeployed(toState, deployed).happen(preDeployGame)
    val A = getPlayerStateByName(toState, postDeployGame)
    "increase the troops in the destination state" in {
      assertResult(A.troops) {
        toPlayerState.troops + deployed
      }
    }
    "reduce the amount of deployable troops" in {
      assertResult(postDeployGame.deployableTroops) {
        preDeployGame.deployableTroops - deployed
      }
    }
  }

  "A card drawn event" should {
    val card = Cards.generateCard()
    val postDrawGame = CardDrawn(p1, card).happen(initialSnapshot)
    "add a card to the correct player" in {
      assertResult(postDrawGame.cards(p1).size) {
        initialSnapshot.cards(p1).size + 1
      }
    }
  }

  "A bonus" when {
    val card = Cards.Artillery
    val cardsToAdd = Seq.fill(3)(card)
    val playerCards = initialSnapshot.cards.getOrElse(p1, Seq.empty[Cards]) ++ cardsToAdd
    val preRedeemGame = initialSnapshot.copy(cards = initialSnapshot.cards + (p1 -> playerCards))
    val postRedeemGame = BonusRedeemed(p1, card).happen(initialSnapshot)
    "redeemed" should {
      "remove 3 cards from the players hand" in {
        assertResult(postRedeemGame.cards(p1).size) {
          preRedeemGame.cards(p1).size - 3
        }
      }
      "give the player extra troops to deploy" in {
        assertResult(postRedeemGame.deployableTroops) {
          preRedeemGame.deployableTroops + card.id
        }
      }
    }
  }

  "A turn" when {
    val game = initialSnapshot.copy(deployableTroops = 0)
    val players = game.players
    val curPlayer = game.nowPlaying
    val curIndex = players.indexOf(curPlayer)
    val nextIndex = (curIndex + 1) % players.size
    val nextPlayer = players(nextIndex)
    val nextDeployableTroops = game.geopolitics.count(p => p.owner.equals(nextPlayer))
    val nextTurn = TurnEnded(curPlayer).happen(game)
    "ended" should {
      "update the current player to be the next in the sequence" in {
        assert(nextTurn.nowPlaying.equals(nextPlayer))
      }
      "update the deployable troops" in {
        assertResult(nextDeployableTroops) {
          nextTurn.deployableTroops
        }
      }
    }
  }
  
  "Upon game ending the winner" should {
    "be present in the snapshot" in {
      val game = GameEnded(p1).happen(initialSnapshot)
      assertResult(Some(p1)) {
        game.winner
      }
    }
  }

  def getPlayerStateByName(name: State, game: GameSnapshot) : PlayerState = {
    game.geopolitics.collectFirst({
      case s if s.state.equals(name) => s
    }).get
  }
}
