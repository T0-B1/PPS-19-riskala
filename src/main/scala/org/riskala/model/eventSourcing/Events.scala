package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.Player
import org.riskala.model.State.State

sealed trait Event

final case class GameInitialized(initialSnapshot: GameSnapshot)
                  extends Event

final case class Battle(from: State,
                       to: State,
                       attacking: Int,
                       attackingPassed: Int,
                       defendingCasualties: Int)
                  extends Event

final case class TroopsMoved(from: State,
                      to: State,
                      troops: Int)
                  extends Event

final case class TroopsDeployed(to: State,
                        troops: Int)
                  extends Event

final case class CardDrawn(player: Player,
                      card: Cards)
                  extends Event

final case class BonusRedeemed(player: Player,
                        cardBonus: Cards)
                  extends Event

final case class TurnEnded(player: Player)
                  extends Event


