package org.riskala.controller.actors

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.player.PlayerMessages.PlayerMessage

object Messages {

  trait LobbyMessage

  trait GameMessage

  trait RoomMessage

  sealed trait LogoutMessage extends LobbyMessage with GameMessage with RoomMessage

  /** Message sent to exit
   * @param actor              The actor who wants to exit
   * */
  case class Logout(actor: ActorRef[PlayerMessage]) extends LogoutMessage
}
