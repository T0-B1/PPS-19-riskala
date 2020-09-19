package org.riskala.model.lobby

import akka.actor.typed.ActorRef
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.model.ModelMessages._
import org.riskala.model.Player
import org.riskala.view.messages.ToClientMessages.{RoomBasicInfo, RoomInfo}


/**
 * lobby messages
 */
object LobbyMessages {
/*
  case class RoomNameInfo(name: String, players: String)
  object RoomNameInfo {
    implicit def RoomNameInfoCodecJson =
      casecodec2(RoomNameInfo.apply,RoomNameInfo.unapply)("name","players")
  }

  /** Information about lobby
   * @param rooms              The list of name of the rooms
   * @param games              The list of name of the games
   * @param terminatedGames    The list of name of the terminated games
   * */
  case class LobbyInfo(rooms: List[RoomNameInfo], games: List[String], terminatedGames: List[String])
  object LobbyInfo {
    implicit def LobbyInfoCodecJson =
      casecodec3(LobbyInfo.apply,LobbyInfo.unapply)("rooms","games","terminatedGames")
  }
*/

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