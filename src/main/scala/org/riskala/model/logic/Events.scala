package org.riskala.model.logic

import org.riskala.model.Cards.Cards
import org.riskala.model.Player
import org.riskala.model.map.State.State

trait Event{
  def happen(game: GameSnapshot): GameSnapshot
}

final case class GameInitialized(initialSnapshot: GameSnapshot)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = game
}

final case class Battle(from: State,
                       to: State,
                       attacking: Int,
                       attackingPassed: Int,
                       defendingCasualties: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    var geopolitics = game.geopolitics
    val attacker = geopolitics.getPlayerStateByName(from).get.owner
    geopolitics = geopolitics.modifyStateTroops(from, -attacking)
    if(attackingPassed > 0) {
      game.copy(geopolitics = geopolitics.setStateTroops(to, attackingPassed)
        .updateStateOwner(to, attacker))
    } else
      game.copy(geopolitics = geopolitics.modifyStateTroops(to, - defendingCasualties))
  }
}

final case class TroopsMoved(from: State,
                             to: State,
                             moved: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    game.copy(geopolitics = game.geopolitics
      .modifyStateTroops(from, -moved)
      .modifyStateTroops(to, +moved))
  }
}

final case class TroopsDeployed(to: State,
                        troops: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    game.copy(geopolitics = game.geopolitics.modifyStateTroops(to, +troops),
      deployableTroops = game.deployableTroops - troops)
  }
}

final case class CardDrawn(player: Player,
                      card: Cards)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    val playerCards = game.cards.getOrElse(player, Seq.empty[Cards]) :+ card
    game.copy(cards = game.cards + (player -> playerCards))
  }
}

final case class BonusRedeemed(player: Player,
                               card: Cards)
                  extends Event {

  private val numberOfCopies: Int = 3

  override def happen(game: GameSnapshot): GameSnapshot = {
    val toRemove = Seq.fill(numberOfCopies)(card)
    val playerCards = game.cards.getOrElse(player, Seq.empty[Cards]) diff toRemove
    val bonus = card.id
    game.copy(cards = game.cards + (player -> playerCards), deployableTroops = game.deployableTroops + bonus)
  }
}

final case class TurnEnded(player: Player)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    val currentPlayerIndex = game.players.indexOf(game.nowPlaying)
    val nextIndex = (currentPlayerIndex + 1) % game.players.size
    val nextPlayer = game.players(nextIndex)
    val nextTurn = game.turn + 1
    game.copy(nowPlaying = nextPlayer, turn = nextTurn, deployableTroops = game.geopolitics.count(p => p.owner.equals(nextPlayer)))
  }
}

final case class GameEnded(winner: Player)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    game.copy(winner = Some(winner))
  }
}

