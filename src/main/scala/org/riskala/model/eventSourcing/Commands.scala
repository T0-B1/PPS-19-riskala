package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
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

final case class RedeemBonus(player: Player,
                             cardBonus: Cards)
                  extends Command

final case class EndTurn(player: Player)
                  extends Command


object Command {

  // TODO decompose and move single behavior into relative commands
  // TODO provide an overload of behavior which takes a snapshot instead of a Seq[Events]

  def behaviorOf(command: Command) : Seq[Event] => Seq[Event] = {
    command match {
      // TODO use projection and/or overload above to verify state before performing behavior
      // TODO maybe introduce scalaz validation
      case Attack(from, to, troops) =>
        _ => Seq(Battle(null,null,0,0,0))
      case MoveTroops(from, to, troops) =>
        _ => Seq(TroopsMoved(null,null,0))
      case Deploy(to, troops) =>
        _ => Seq(TroopsDeployed(null,0))
      case RedeemBonus(player, cardBonus) =>
        _ => Seq(BonusRedeemed(player, cardBonus))
      case EndTurn(player) =>
        _ => Seq(TurnEnded(player))
    }
  }

}