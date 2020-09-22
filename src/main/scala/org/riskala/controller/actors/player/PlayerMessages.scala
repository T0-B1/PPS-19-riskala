package org.riskala.controller.actors.player

import akka.actor
import akka.actor.typed.ActorRef
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, RoomMessage}
import org.riskala.model.Player
import org.riskala.model.map.{MapGeography, PlayerState}
import org.riskala.view.messages.ToClientMessages.{GamePersonalInfo, LobbyInfo, RoomInfo}

object PlayerMessages {

  trait PlayerMessage

  /**
   * Message which is sent to or received from the socket
   * @param payload The the string in the JSON format of the message
   * */
  final case class SocketMessage(payload: String) extends PlayerMessage

  /**
   * Message used to register a new socker
   * @param socketActor Classic Akka Actor which handles the socket
   * */
  final case class RegisterSocket(socketActor: actor.ActorRef) extends PlayerMessage

  /**
   * Message used to notify that the new referent for this actor is the Lobby
   * @param lobby The actorRef of the lobby
   * */
  final case class LobbyReferent(lobby: ActorRef[LobbyMessage]) extends PlayerMessage

  /**
   * Message used to notify that the new referent for this actor is the ROOM
   * @param room The actorRef of the room
   * */
  final case class RoomReferent(room: ActorRef[RoomMessage]) extends PlayerMessage

  /**
   * Message used to notify that the new referent for this actor is the Game
   * @param game The actorRef of the game
   * */
  final case class GameReferent(game: ActorRef[GameMessage]) extends PlayerMessage

  /**
   * Message used to send to the playerActor the new room information
   * @param roomInfo The information about the room
   * */
  final case class RoomInfoMessage(roomInfo: RoomInfo) extends PlayerMessage

  /**
   * Message used to send to the playerActor the new lobby information
   * @param lobbyInfo The information about the lobby
   * */
  final case class LobbyInfoMessage(lobbyInfo: LobbyInfo) extends PlayerMessage

  /**
   * Message used to send to the playerActor the whole game information
   * @param players Set of players into the game
   * @param actualPlayer The player who is playing
   * @param troopsToDeploy The max number of troops that the actual player can deploy
   * @param map Information about the map on which players will play
   * @param isDeployOnly Represents first turn for all players
   * @param playerStates The information about states (ownership and number of troops)
   * @param personalInfo Information strictly related to a specific player (cards and objectives)
   * @param winner  The winner player, if the game is terminated
   * */
  final case class GameInfoMessage(players: Set[String],
                                   actualPlayer: String,
                                   troopsToDeploy: Int,
                                   map: MapGeography,
                                   isDeployOnly: Boolean,
                                   playerStates: Set[PlayerState],
                                   personalInfo: GamePersonalInfo,
                                   winner: Option[Player]) extends PlayerMessage

  /**
   * Message used to send to the playerActor the updated game information
   * @param actualPlayer The player who is playing
   * @param troopsToDeploy The max number of troops that the actual player can deploy
   * @param isDeployOnly Represents first turn for all players
   * @param playerStates The information about states (ownership and number of troops)
   * @param personalInfo Information strictly related to a specific player (cards and objectives)
   * */
  final case class GameUpdateMessage(actualPlayer: String,
                                     troopsToDeploy: Int,
                                     isDeployOnly: Boolean,
                                     playerStates: Set[PlayerState],
                                     personalInfo: GamePersonalInfo) extends PlayerMessage

  /**
   * Message sent to all player which notify the victory of a player
   * @param winner The player who won
   * */
  final case class GameEndMessage(winner: Player) extends PlayerMessage

  /**
   * Notify a generic error
   * @param error The occurred error
   * */
  final case class ErrorMessage(error: String) extends PlayerMessage

}
