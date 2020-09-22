package org.riskala.controller.actors.lobby

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.player.PlayerMessages.PlayerMessage
import org.riskala.controller.actors.Messages._
import org.riskala.model.Player
import org.riskala.client.messages.ToClientMessages.{RoomBasicInfo, RoomInfo}


/**
 * lobby messages
 */
object LobbyMessages {
  /** Message sent to subscribe to the lobby
   * @param subscriber              The actor who wants to subscribe to the lobby
   * */
  case class Subscribe(subscriber: ActorRef[PlayerMessage]) extends LobbyMessage

  /** Message sent to create a new room
   * @param creator              The actor who creates the room
   * @param roomInfo             The room information
   * */
  case class CreateRoom(creator: ActorRef[PlayerMessage], roomInfo: RoomInfo) extends LobbyMessage

  /** Message sent to join a room
   * @param actor              The actor who wants to join the room
   * @param name               The name of the room to join
   * */
  case class JoinTo(actor: ActorRef[PlayerMessage], name: String) extends LobbyMessage

  /** Message sent when a room has reached the maximum number of players
   * @param info              All the information of the room in which the game will be played
   * @param players           The list of players actor ref who will play the game
   * @param roomSubscribers   the list of subscribers actor ref who will spectate the game
   * */
  case class StartGame(info: RoomInfo,
                       players: Map[Player,ActorRef[PlayerMessage]],
                       roomSubscribers: Set[ActorRef[PlayerMessage]]) extends LobbyMessage

  /** Message sent when a game ends
   * @param name             The name of the game that ended
   * @param game             The game that ended
   * */
  case class EndGame(name: String, game: ActorRef[GameMessage]) extends LobbyMessage

  /** Message sent when GameManager ends
   * @param name             The name of the game that ended
   * @param subscribers      List the game's players
   * */
  case class GameClosed(name: String, subscribers: Seq[ActorRef[PlayerMessage]]) extends LobbyMessage

  /** Message sent to update room information
   * @param info             Room information
   * */
  case class UpdateRoomInfo(info:RoomBasicInfo) extends LobbyMessage

  /**
   * Message sent to close room
   * @param roomName      The name of the room to close
   * */
  case class EmptyRoom(roomName: String) extends LobbyMessage

}
