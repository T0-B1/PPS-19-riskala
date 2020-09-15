package org.riskala.model.events

import org.riskala.model.{Player, State}
import org.riskala.model.State.State

object Events {

  sealed trait Event

  final case class Attack(from: State,
                         to: State,
                         attacking: Int,
                         attackingPassed: Int,
                         defendingCasualties: Int)

  final case class Move(from: State,
                        to: State,
                        troops: Int)

  final case class Deploy(to: State,
                          troops: Int)

  // TODO use card
  final case class Draw(player: Player,
                        card: Unit)

  // TODO use card
  final case class RedeemBonus(player: Player,
                          cardBonus: Unit)

}
