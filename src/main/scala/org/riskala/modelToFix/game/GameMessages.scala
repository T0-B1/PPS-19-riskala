package org.riskala.modelToFix.game

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.modelToFix.Cards.Cards
import org.riskala.modelToFix.ModelMessages.GameMessage
import org.riskala.modelToFix.ModelMessages.GameMessage

import scala.collection.immutable.Queue

/**
 * Type of possible messages that GameManager can receive
 * */
object GameMessages {

  /**
   * Message sent when an actor wants to join this game
   * */
  case class JoinGame(actor: ActorRef[PlayerMessage]) extends GameMessage

  /**
   * Message sent when an actor wants to leave this game
   * */
  case class Leave(actor: ActorRef[PlayerMessage]) extends GameMessage

  /**
   * Message sent when an actor wants to do an action.
   * Action type: Move, Deploy or Attack
   * */
  case class Action(playerName: String, from: Int, to: Int, troops: Int) extends GameMessage

  /**
   * Message sent when an actor wants to redeem bonus
   * */
  case class RedeemBonus(playerName: String, card: Cards) extends GameMessage

  /**
   * Message sent when an actor ends his turn
   * */
  case class EndTurn(playerName: String) extends GameMessage
}
