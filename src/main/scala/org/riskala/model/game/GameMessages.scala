package org.riskala.model.game

import org.riskala.model.ModelMessages.GameMessage

import scala.collection.immutable.Queue

object GameMessages {

  case class GameInfo(name: String, scenario: String, players: Queue[String])

  /**
   * Message sent when an actor wants to join this game
   * */
  case class Join() extends GameMessage

  /**
   * Message sent when an actor wants to leave this game
   * */
  case class Leave() extends GameMessage

  /**
   * Message sent when an actor wants to deploy troops
   * */
  case class Deploy() extends GameMessage

  /**
   * Message sent when an actor wants to attack
   * */
  case class Attack() extends GameMessage

  /**
   * Message sent when an actor wants to move troops
   * */
  case class Move() extends GameMessage

  /**
   * Message sent when an actor wants to redeem bonus
   * */
  case class RedeemBonus() extends GameMessage

  /**
   * Message sent when an actor ends his turn
   * */
  case class EndTurn() extends GameMessage
}
