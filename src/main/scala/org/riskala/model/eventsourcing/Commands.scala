package org.riskala.model.eventsourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.{Cards, Player}
import org.riskala.model.State.State
import org.riskala.model.eventsourcing.EventStore.Behavior

import scala.util.Random

sealed trait Command{
  def execution(game: GameSnapshot): Behavior[Event]

  def feasibility(game: GameSnapshot): FeasibilityReport
}

trait SelfCheckingCommand extends Command {
  override def execution(game: GameSnapshot): Behavior[Event] = {
    val feasibilityReport = feasibility(game)
    if(!feasibilityReport.feasible) {
      println(feasibilityReport.error)
      return Seq.empty
    }
    checkedExecution(game)
  }

  def checkedExecution(game: GameSnapshot): Behavior[Event]
}

case class FeasibilityReport(feasible: Boolean = true, error: Option[String] = None)

final case class Attack(from: State,
                        to: State,
                        troops: Int)
                  extends SelfCheckingCommand {

  private val rng = new Random()
  private val rndUpperBound: Int = 10
  private val defenderThreshold: Int = 5

  override def checkedExecution(game: GameSnapshot): Behavior[Event] = {
    val defenders = game.geopolitics.getPlayerStateByName(to).get.troops
    var remainingDefenders = defenders
    var remainingAttackers = troops
    while(remainingAttackers > 0 && remainingDefenders > 0) {
      if(rng.nextInt(rndUpperBound) > defenderThreshold)
        remainingDefenders = remainingDefenders - 1
      else
        remainingAttackers = remainingAttackers - 1
    }
    if(remainingAttackers == 0)
      Seq(Battle(from, to, troops, 0, defenders - remainingDefenders))
    else{
      val attacker = game.geopolitics.getPlayerStateByName(from).get.owner
      val events: Seq[Event] = Seq.empty :+
        Battle(from, to, troops, remainingAttackers, defenders) :+
        CardDrawn(attacker, Cards.generateCard())
      val objective: Set[State] = game.objectives(attacker).states
      val conquered: Set[State] = game.geopolitics.getStatesOfPlayer(attacker)
      // The ownership of the only state left is yet to be updated, but if it is the one just conquered, it's a victory
      val diff: Set[State] = objective.diff(conquered)
      if(diff.size == 1 && diff.head.equals(to))
        events :+ GameEnded(attacker)
      else
        events
    }
  }

  override def feasibility(game: GameSnapshot): FeasibilityReport = {
    val fromPS = game.geopolitics.getPlayerStateByName(from)
    if(fromPS.isEmpty)
      return FeasibilityReport(feasible = false, Some(s"Attacking from an unknown state: $from"))
    val toPS = game.geopolitics.getPlayerStateByName(to)
    if(toPS.isEmpty)
      return FeasibilityReport(feasible = false, Some(s"Attacking an unknown state: $to"))
    val turnFeasibility = Command.checkTurn(fromPS.get.owner, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    val availableTroops = fromPS.get.troops
    if(availableTroops <= troops)
      return FeasibilityReport(feasible = false, Some(s"Insufficient troops ($availableTroops) for attack with $troops from $from"))
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
    val fromPS = game.geopolitics.getPlayerStateByName(from)
    if(fromPS.isEmpty)
      return FeasibilityReport(feasible = false, Some(s"Moving from an unknown state: $from"))
    val toPS = game.geopolitics.getPlayerStateByName(to)
    if(toPS.isEmpty)
      return FeasibilityReport(feasible = false, Some(s"Moving to an unknown state: $to"))
    val turnFeasibility = Command.checkTurn(fromPS.get.owner, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    val availableTroops = fromPS.get.troops
    if(availableTroops <= troops)
      return FeasibilityReport(feasible = false, Some(s"Insufficient troops ($availableTroops) for attack with $troops from $from"))
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
    val toPS = game.geopolitics.getPlayerStateByName(to)
    if(toPS.isEmpty)
      return FeasibilityReport(feasible = false, Some(s"Moving to an unknown state: $to"))
    val turnFeasibility = Command.checkTurn(toPS.get.owner, game)
    if(!turnFeasibility.feasible)
      return turnFeasibility
    if(troops > game.deployableTroops)
      return FeasibilityReport(feasible = false, Some(s"Not enough troops (${game.deployableTroops}) to deploy $troops to state $to"))
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
      return FeasibilityReport(feasible = false, Some(s"No deck found"))
    if(playerCards.get.count(c => c.equals(cardBonus)) < 3)
      return FeasibilityReport(feasible = false, Some(s"Not enough cards of type $cardBonus"))
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
      case p if p.equals(player) => FeasibilityReport(feasible = true, None)
      case _ => FeasibilityReport(feasible = false, Some("Not your turn"))
    }
  }

}