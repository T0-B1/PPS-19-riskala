package org.riskala.model.game

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.model.Cards.Cards
import org.riskala.model.ModelMessages.GameMessage

/**
 * Type of possible messages that GameManager can receive
 * */
object GameMessages {

  /**
   * Message sent when a player wants to join this game
   * @param actor The player that wants to join the game
   * */
  case class JoinGame(actor: ActorRef[PlayerMessage]) extends GameMessage

  /**
   * Message sent when an actor wants to leave this game
   * @param actor The actor that wants to leave the game
   * */
  case class Leave(actor: ActorRef[PlayerMessage]) extends GameMessage

  /**
   * Message sent when an actor wants to do an attack.
   * @param playerName The actual player
   * @param from The state of departure
   * @param to The arrival state
   * @param troops The troops that the player decided to use for the action
   * */
  case class ActionAttack(playerName: String, from: String, to: String, troops: Int) extends GameMessage

  /**
   * Message sent when an actor wants to do a move.
   * @param playerName The actual player
   * @param from The state of departure
   * @param to The arrival state
   * @param troops The troops that the player decided to use for the action
   * */
  case class ActionMove(playerName: String, from: String, to: String, troops: Int) extends GameMessage

  /**
   * Message sent when an actor wants to do a deploy.
   * Action type: Move, Deploy or Attack
   * @param playerName The actual player
   * @param from The state of departure
   * @param to The arrival state
   * @param troops The troops that the player decided to use for the action
   * */
  case class ActionDeploy(playerName: String, from: String, to: String, troops: Int) extends GameMessage

  /**
   * Message sent when an actor wants to redeem bonus
   * @param playerName The actual player
   * @param card The random card redeemed
   * */
  case class RedeemBonus(playerName: String, card: Cards) extends GameMessage

  /**
   * Message sent when an actor ends his turn
   * @param playerName The player who ended his turn
   * */
  case class EndTurn(playerName: String) extends GameMessage

  /**
   * Message sent when an actor need all the game info
   * @param playerName The name of the possible asking actor
   * @param actor The actor who receives the game information
   * */
  case class GetFullInfo(playerName: String, actor: ActorRef[PlayerMessage]) extends GameMessage
}
