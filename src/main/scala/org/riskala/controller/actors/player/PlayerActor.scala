package org.riskala.controller.actors.player

import akka.actor
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.player.PlayerMessages.PlayerMessage
import org.riskala.controller.actors.lobby.LobbyManager.lobbyServiceKey

object PlayerActor {

  private final case class AdaptedListing(listing: Receptionist.Listing) extends PlayerMessage

  /**
   * Apply of PlayerActor that returns a playerLobbyBehavior
   * @param username  Username of the player
   * @param socket Classic Akka Actor which handles a socket
   * */
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
              LobbyPlayerBehavior(username, lobbyManager, socket)
            case None => {
              context.log.error("Lobby manager not found")
              Behaviors.same
            }
          }
      }
    }

}
