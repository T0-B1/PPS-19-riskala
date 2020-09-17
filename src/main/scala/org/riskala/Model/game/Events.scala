package org.riskala.model.game

import org.riskala.model.Player
import org.riskala.model.State.State

sealed trait Event

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

// TODO use card
final case class CardDrawn(player: Player,
                      card: Unit)
                  extends Event

// TODO use card
final case class BonusRedeemed(player: Player,
                        cardBonus: Unit)
                  extends Event

final case class TurnEnded(player: Player)
                  extends Event


