package org.riskala.Model

import akka.actor.ActorRef


/**
 * Model messages
 */
object ModelMessages {

  trait LobbyMessage

  trait GameMessage

  sealed trait RoomMessage

  /** Message sent when an actor is ready in a room
   *  @param playerName     The player who is ready in a room
   *  @param actor          The reference of the player
   * */
  case class Ready(playerName: String, actor: ActorRef) extends RoomMessage

  /** Message sent when an actor wants to participate to a game
   * @param actor           The actor who want to participate to a game
   * */
  case class Join(actor: ActorRef) extends GameMessage with RoomMessage

  /** Message sent when an actor wants to leave a game
   * @param actor           The actor who want to leave a game
   * */
  case class Leave(actor: ActorRef) extends GameMessage with RoomMessage


  sealed trait LogoutMessage extends LobbyMessage with GameMessage with RoomMessage

  /** Message sent to exit
   * @param actor              The actor who wants to exit
   * */
  case class Logout(actor: ActorRef) extends LogoutMessage
}
