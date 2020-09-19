package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.Player
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
  override def happen(game: GameSnapshot): GameSnapshot = game
}

final case class TroopsMoved(from: State,
                      to: State,
                      troops: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = game
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
                        cardBonus: Cards)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    val toRemove = Seq.fill(3)(cardBonus)
    val playerCards = game.cards.getOrElse(player, Seq.empty[Cards]) diff toRemove
    game.copy(cards = game.cards + (player -> playerCards))
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

