package org.riskala.model.game

import org.riskala.model.Player
import org.riskala.model.State.State

sealed trait Command

final case class Attack(from: State,
                        to: State,
                        troops: Int)
                  extends Command

final case class MoveTroops(from: State,
                            to: State,
                            troops: Int)
                  extends Command

final case class Deploy(to: State,
                        troops: Int)
                  extends Command

// TODO use card
final case class RedeemBonus(player: Player,
                             cardBonus: Unit)
                  extends Command

final case class EndTurn(player: Player)
                  extends Command
