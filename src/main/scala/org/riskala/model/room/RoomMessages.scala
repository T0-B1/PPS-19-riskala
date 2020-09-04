package org.riskala.model.room

import akka.actor.typed.ActorRef
import org.riskala.model.ModelMessages._

/**
 * Room messages
 */
object RoomMessages {

  /**
   * @param name                    Room name
   * @param actualNumberOfPlayer    Number of player in a room
   * @param maxNumberOfPlayer       Maximun numbere of player of a room
   * */
  case class RoomBasicInfo(name: String,
                           actualNumberOfPlayer: Int,
                           maxNumberOfPlayer: Int)

  /**
   * @param basicInfo               Object containing the basic information of a room
   * @param scenario                Name of the game map
   * */
  case class RoomInfo(basicInfo: RoomBasicInfo, scenario: String)

  /** Message sent when an actor is ready in a room
   *  @param playerName     The player who is ready in a room
   *  @param actor          The reference of the player
   * */
  case class Ready(playerName: String, actor: ActorRef[PlayerMessage])
    extends RoomMessage

  /** Message sent when an actor is not ready anymore
   *  @param playerName     The player name who is not ready
   *  @param actor          The reference of the player
   * */
  case class UnReady(playerName: String, actor: ActorRef[PlayerMessage])
    extends RoomMessage

  /** Message sent when an actor wants to participate to a game
   * @param actor           The actor who want to participate to a game
   * */
  case class Join(actor: ActorRef[PlayerMessage]) extends GameMessage with RoomMessage

  /** Message sent when an actor wants to leave a game
   * @param actor           The actor who want to leave a game
   * */
  case class Leave(actor: ActorRef[PlayerMessage]) extends GameMessage with RoomMessage

}
