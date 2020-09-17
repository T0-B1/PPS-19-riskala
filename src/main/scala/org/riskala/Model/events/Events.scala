package org.riskala.model.events

import org.riskala.model.Player
import org.riskala.model.State.State

sealed trait Event

final case class Attack(from: State,
                       to: State,
                       attacking: Int,
                       attackingPassed: Int,
                       defendingCasualties: Int)
                  extends Event

final case class Move(from: State,
                      to: State,
                      troops: Int)
                  extends Event

final case class Deploy(to: State,
                        troops: Int)
                  extends Event

// TODO use card
final case class Draw(player: Player,
                      card: Unit)
                  extends Event

// TODO use card
final case class RedeemBonus(player: Player,
                        cardBonus: Unit)
                  extends Event

final case class EndTurn(player: Player)
                  extends Event


