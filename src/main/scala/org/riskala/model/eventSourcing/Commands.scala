package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.{Geopolitics, Player}
import org.riskala.model.State.State
import org.riskala.model.eventSourcing.EventStore.Behavior

sealed trait Command{
  def execution(game: GameSnapshot): Behavior[Event]

  def feasibility(game: GameSnapshot): FeasibilityReport
}

trait SelfCheckingCommand extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = {
    val feasibilityReport = feasibility(game)
    if(!feasibilityReport.feasible)
      return Seq.empty
    checkedExecution(game)
  }

  def checkedExecution(game: GameSnapshot): Behavior[Event]
}

case class FeasibilityReport(feasible: Boolean = true, error: Option[String] = None)

final case class Attack(from: State,
                        to: State,
                        troops: Int)
                  extends SelfCheckingCommand {
  override def checkedExecution(game: GameSnapshot): Behavior[Event] = Seq.empty

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
                  extends SelfCheckingCommand {
  override def checkedExecution(game: GameSnapshot): Behavior[Event] = {
    Seq(TroopsMoved(from, to, troops))
  }

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
                  extends SelfCheckingCommand {
  override def checkedExecution(game: GameSnapshot): Behavior[Event] = {
    Seq(TroopsDeployed(to, troops))
  }

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
                  extends SelfCheckingCommand {
  override def checkedExecution(game: GameSnapshot): Behavior[Event] = {
    Seq(BonusRedeemed(player, cardBonus))
  }

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
                  extends SelfCheckingCommand {
  override def checkedExecution(game: GameSnapshot): Behavior[Event] = {
    Seq(TurnEnded(player))
  }

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    Command.checkTurn(player, game)
  }
}

object Command {

  def checkTurn(player: Player, game: GameSnapshot): FeasibilityReport = {
    game.nowPlaying match {
      case p if p.equals(player) => FeasibilityReport(true, None)
      case _ => FeasibilityReport(false, Some("Not your turn"))
    }
  }

}