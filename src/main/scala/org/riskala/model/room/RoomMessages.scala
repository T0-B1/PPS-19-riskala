package org.riskala.model.room

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.model.ModelMessages._
import org.riskala.model.Player

/**
 * Room messages
 */
object RoomMessages {

  /** Message sent when an actor is ready in a room
   *  @param player     The name of the player who is ready in a room
   *  @param actor          The player ready in  a room
   * */
  case class Ready(player: Player, actor: ActorRef[PlayerMessage]) extends RoomMessage

  /** Message sent when an actor is not ready anymore
   *  @param playerName     The player name who is not ready
   *  @param actor          The reference of the player
   * */
  case class UnReady(playerName: String, actor: ActorRef[PlayerMessage]) extends RoomMessage

  /** Message sent when an actor wants to participate to a game
   * @param actor           The actor who wants to participate to a game
   * */
  case class Join(actor: ActorRef[PlayerMessage]) extends GameMessage with RoomMessage

  /** Message sent when an actor wants to leave a game
   * @param actor           The actor who wants to leave a game
   * */
  case class Leave(actor: ActorRef[PlayerMessage]) extends GameMessage with RoomMessage

}
