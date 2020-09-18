package org.riskala.controller.actors

import akka.actor
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.modelToFix.lobby.LobbyManager._

object PlayerActor {

  private final case class AdaptedListing(listing: Receptionist.Listing) extends PlayerMessage

  def apply(username: String, socket: actor.ActorRef): Behavior[PlayerMessage] = {
    discoverLobbyManager(username, socket)
  }

  private def discoverLobbyManager(username: String, socket: actor.ActorRef): Behavior[PlayerMessage] =
    Behaviors.setup { context =>
      context.log.info("PlayerActor looking for Lobby")
      context.system.receptionist ! Receptionist.Find(lobbyServiceKey, context.messageAdapter[Receptionist.Listing](AdaptedListing))

      Behaviors.receiveMessage {
        case AdaptedListing(lobbyServiceKey.Listing(listings)) =>
          listings.headOption match {
            case Some(lobbyManager) =>
              context.log.info("PlayerActor found Lobby")
              PlayerLobbyBehavior(username, lobbyManager, socket)
            case None => {
              context.log.error("Lobby manager not found")
              Behaviors.same
            }
          }
      }
    }

}
