package org.riskala.model.lobby

import akka.actor.typed.ActorRef
import org.riskala.model.ModelMessages._

/**
 * Lobby messages
 */
object LobbyMessages {

  /**
   * @param name                    Room name
   * @param actualNumberOfPlayer    Number of players in a room
   * @param maxNumberOfPlayer       Maximum number of players in a room
   * */
  case class RoomBasicInfo(name: String,
                      actualNumberOfPlayer: Int,
                      maxNumberOfPlayer: Int)

  /**
   * @param basicInfo               Object containing basic information of a room
   * @param scenario                Name of the game map
   * */
  case class RoomInfo(basicInfo: RoomBasicInfo, scenario: String)

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
   * @param name              The name of the room in which the game will be played
   * @param actor             The actor who is starting the game
   * */
  case class StartGame(name: String, actor: ActorRef[GameMessage]) extends LobbyMessage

  /** Message sent when a game ends
   * @param name             The name of the game that ended
   * @param game             The game that ended
   * */
  case class EndGame(name: String, game: ActorRef[GameMessage]) extends LobbyMessage

  /** Message sent when GameManager ends
   * @param name             The name of the game that ended
   * @param subscribers      List the game's players
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
