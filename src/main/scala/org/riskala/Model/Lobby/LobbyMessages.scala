package org.riskala.model.lobby

import akka.actor.typed.ActorRef
import org.riskala.model.ModelMessages._
import org.riskala.model.room.RoomMessages.{RoomBasicInfo, RoomInfo}

/**
 * Lobby messages
 */
object LobbyMessages {

  /** Message sent to subscribe himself to the lobby
   * @param subscriber              The actor
   * */
  case class Subscribe(subscriber: ActorRef[PlayerMessage]) extends LobbyMessage

  /** Message sent to create a new room
   * @param creator              The actor who create a room
   * @param roomInfo             The room information
   * */
  case class CreateRoom(creator: ActorRef[PlayerMessage], roomInfo: RoomInfo) extends LobbyMessage

  /** Message sent to join to a room
   * @param actor              The actor who wants to join the room
   * @param name               The name of the room to join
   * */
  case class JoinTo(actor: ActorRef[PlayerMessage], name: String) extends LobbyMessage

  /** Message sent when a room has reached the max number of player
   * @param name              The name of the room in which i will play a game
   * @param actor             The actor who will play a game
   * */
  case class StartGame(info: RoomInfo, actor: ActorRef[GameMessage]) extends LobbyMessage

  /** Message sent when a game ends
   * @param name             The name of game that ended
   * @param game             The game ended
   * */
  case class EndGame(name: String, game: ActorRef[GameMessage]) extends LobbyMessage

  /** Message sent when GameManager ends
   * @param name             The name of game that ended
   * @param subscribers      List of player of the game
   * */
  case class GameClosed(name: String, subscribers: List[ActorRef[PlayerMessage]]) extends LobbyMessage

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
