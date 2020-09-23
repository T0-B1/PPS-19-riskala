package org.riskala.model.logic

import org.riskala.model.Cards.Cards
import org.riskala.model.{Cards, Player}
import org.riskala.model.map.State.State
import org.riskala.model.logic.EventStore.Behavior

import scala.util.Random

sealed trait Command{

  /**
   * Return the result of the execution of the command on a game instance in terms of events that will happen
   *
   * @param game A game instance on which the command is executed
   * @return A behavior
   */
  def execution(game: GameSnapshot): Behavior[Event]

  /**
   * Investigates the feasibility of a command given a game instance and returns a feasibility report
   *
   * @param game A game instance on which the command has to be executed
   * @return A feasibility report
   */
  def feasibility(game: GameSnapshot): FeasibilityReport

}

case class FeasibilityReport(feasible: Boolean = true, error: Option[String] = None)

/**
 * A command that automatically checks for feasibility before being executed
 */
trait SelfCheckingCommand extends Command {

  /**
   * Only in case the command is feasible, it returns the result of the execution of the command on a game instance
   * in terms of events that will happen.
   *
   * @param game A game instance on which the command is executed
   * @return A behavior
   */
  override def execution(game: GameSnapshot): Behavior[Event] = {
    val feasibilityReport = feasibility(game)
    if(!feasibilityReport.feasible) {
      println(feasibilityReport.error)
      return Seq.empty
    }
    checkedExecution(game)
  }

  /**
   * The execution of the command that will happen if feasible
   *
   * @param game A game instance on which the command is executed
   * @return A behavior
   */
  def checkedExecution(game: GameSnapshot): Behavior[Event]
}

/**
 * An attack
 *
 * @param from The attacking state
 * @param to The target state
 * @param troops The number of troops mobilized
 */
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

/**
 * A command to move troops between states of the same player
 * @param from The state of origin
 * @param to The destination state
 * @param troops The number of troops to move
 */
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

/**
 * A command to deploy troops to a state
 * @param to The target state
 * @param troops The number of troops to deploy
 */
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

/**
 * A command to redeem a card bonus
 * @param player The owner of the cards
 * @param cardBonus The type of bonus to redeem
 */
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

/**
 * A command to end the turn of a player
 * @param player The player whose turn ends
 */
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

  /**
   * Check that a command is being executed by the user whose current turn is
   *
   * @param player The player executing the command
   * @param game The game
   * @return A feasibility report
   */
  def checkTurn(player: Player, game: GameSnapshot): FeasibilityReport = {
    game.nowPlaying match {
      case p if p.equals(player) => FeasibilityReport(feasible = true, None)
      case _ => FeasibilityReport(feasible = false, Some("Not your turn"))
    }
  }

}