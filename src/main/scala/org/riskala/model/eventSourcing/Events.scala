package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.Player
import org.riskala.model.State.State

trait Event{
  def happen(snapshot: GameSnapshot): GameSnapshot
}

final case class GameInitialized(initialSnapshot: GameSnapshot)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

final case class Battle(from: State,
                       to: State,
                       attacking: Int,
                       attackingPassed: Int,
                       defendingCasualties: Int)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

final case class TroopsMoved(from: State,
                      to: State,
                      troops: Int)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

final case class TroopsDeployed(to: State,
                        troops: Int)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

final case class CardDrawn(player: Player,
                      card: Cards)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

final case class BonusRedeemed(player: Player,
                        cardBonus: Cards)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

final case class TurnEnded(player: Player)
                  extends Event {
  override def happen(snapshot: GameSnapshot): GameSnapshot = snapshot
}

