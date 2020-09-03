package org.riskala.Model

import akka.actor.typed.ActorRef

object ModelMessages {

  trait LobbyMessage

  trait GameMessage

  trait RoomMessage

  //TODO: move to Controller
  trait PlayerMessage

  sealed trait LogoutMessage extends LobbyMessage with GameMessage with RoomMessage

  /** Message sent to exit
   * @param actor              The actor who wants to exit
   * */
  case class Logout(actor: ActorRef[PlayerMessage]) extends LogoutMessage
}
