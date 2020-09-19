package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.{Geopolitics, Player}
import org.riskala.model.State.State
import org.riskala.model.eventSourcing.EventStore.Behavior

sealed trait Command{
  def execution(game: GameSnapshot): Behavior[Event]

  def feasibility(game: GameSnapshot): FeasibilityReport
}

case class FeasibilityReport(feasible: Boolean = true, error: Option[String] = None)

final case class Attack(from: State,
                        to: State,
                        troops: Int)
                  extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = e => e

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    val fromPS = game.geopolitics.getPlayerState(from)
    if(fromPS.isEmpty)
      return FeasibilityReport(false, Some(s"Attacking from an unknown state: $from"))
    val toPS = game.geopolitics.getPlayerState(to)
    if(toPS.isEmpty)
      return FeasibilityReport(false, Some(s"Attacking an unknown state: $to"))
    val turnFeasibility = Command.checkTurn(fromPS.get.owner, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    val availableTroops = fromPS.get.troops
    if(availableTroops <= troops)
      return FeasibilityReport(false, Some(s"Insufficient troops ($availableTroops) for attack with $troops from $from"))
    FeasibilityReport()
  }
}

final case class MoveTroops(from: State,
                            to: State,
                            troops: Int)
                  extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = e => e

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    val fromPS = game.geopolitics.getPlayerState(from)
    if(fromPS.isEmpty)
      return FeasibilityReport(false, Some(s"Moving from an unknown state: $from"))
    val toPS = game.geopolitics.getPlayerState(to)
    if(toPS.isEmpty)
      return FeasibilityReport(false, Some(s"Moving to an unknown state: $to"))
    val turnFeasibility = Command.checkTurn(fromPS.get.owner, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    val availableTroops = fromPS.get.troops
    if(availableTroops <= troops)
      return FeasibilityReport(false, Some(s"Insufficient troops ($availableTroops) for attack with $troops from $from"))
    FeasibilityReport()
  }
}

final case class Deploy(to: State,
                        troops: Int)
                  extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = e => e

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    val toPS = game.geopolitics.getPlayerState(to)
    if(toPS.isEmpty)
      return FeasibilityReport(false, Some(s"Moving to an unknown state: $to"))
    val turnFeasibility = Command.checkTurn(toPS.get.owner, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    if(troops > game.deployableTroops)
      return FeasibilityReport(false, Some(s"Not enough troops (${game.deployableTroops}) to deploy $troops to state $to"))
    FeasibilityReport()
  }
}

final case class RedeemBonus(player: Player,
                             cardBonus: Cards)
                  extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = e => e

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    val turnFeasibility = Command.checkTurn(player, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    val playerCards = game.cards.get(player)
    if(playerCards.isEmpty)
      return FeasibilityReport(false, Some(s"No deck found"))
    if(playerCards.get.count(c => c.equals(cardBonus)) < 3)
      return FeasibilityReport(false, Some(s"Not enough cards of type $cardBonus"))
    FeasibilityReport()
  }
}

final case class EndTurn(player: Player)
                  extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = e => e

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    Command.checkTurn(player, game)
  }
}


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

  def checkTurn(player: Player, game: GameSnapshot): FeasibilityReport = {
    game.nowPlaying match {
      case p if p.equals(player) => FeasibilityReport(true, None)
      case _ => FeasibilityReport(false, Some("Not your turn"))
    }
  }

}