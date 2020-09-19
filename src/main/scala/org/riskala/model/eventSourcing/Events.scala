package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.{Geopolitics, Player, PlayerState}
import org.riskala.model.State.State

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
    var fromPS = Geopolitics.getPlayerState(from, geopolitics).get
    var toPS = Geopolitics.getPlayerState(to, geopolitics).get
    fromPS = fromPS.copy(troops = fromPS.troops - attacking)
    if(attackingPassed > 0)
      toPS = toPS.copy(owner = fromPS.owner, troops = attackingPassed)
    else
      toPS = toPS.copy(troops = toPS.troops - defendingCasualties)
    geopolitics = Geopolitics.updateGeopolitics(fromPS, geopolitics)
    geopolitics = Geopolitics.updateGeopolitics(toPS, geopolitics)
    game.copy(geopolitics = geopolitics)
  }
}

final case class TroopsMoved(from: State,
                             to: State,
                             moved: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    var geopolitics = game.geopolitics
    var fromPS = Geopolitics.getPlayerState(from, geopolitics).get
    var toPS = Geopolitics.getPlayerState(to, geopolitics).get
    fromPS = fromPS.copy(troops = fromPS.troops - moved)
    toPS = toPS.copy(troops = toPS.troops + moved)
  }
}

final case class TroopsDeployed(to: State,
                        troops: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = game
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
  override def happen(game: GameSnapshot): GameSnapshot = {
    val toRemove = Seq.fill(3)(card)
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
    game.copy(nowPlaying = game.players(nextIndex))
  }
}

