package org.riskala.model

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.player.PlayerMessages.PlayerMessage

object ModelMessages {

  trait LobbyMessage

  trait GameMessage

  trait RoomMessage

  sealed trait LogoutMessage extends LobbyMessage with GameMessage with RoomMessage

  /** Message sent to exit
   * @param actor              The actor who wants to exit
   * */
  case class Logout(actor: ActorRef[PlayerMessage]) extends LogoutMessage
}
