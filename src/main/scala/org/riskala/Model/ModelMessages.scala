package org.riskala.Model

import akka.actor.typed.ActorRef

/**
 * Model messages
 */
object ModelMessages {

  trait PlayerMessage

  trait LobbyMessage

  trait GameMessage

  sealed trait RoomMessage

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


  sealed trait LogoutMessage extends LobbyMessage with GameMessage with RoomMessage

  /** Message sent to exit
   * @param actor              The actor who wants to exit
   * */
  case class Logout(actor: ActorRef[PlayerMessage]) extends LogoutMessage
}
