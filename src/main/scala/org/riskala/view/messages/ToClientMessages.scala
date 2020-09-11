package org.riskala.view.messages

import argonaut.Argonaut._

object ToClientMessages {

  /**
   * @param name                    Room name
   * @param actualNumberOfPlayer    Number of players in a room
   * @param maxNumberOfPlayer       Maximum number of players in a room
   * */
  case class RoomBasicInfo(name: String,
                           actualNumberOfPlayer: Int,
                           maxNumberOfPlayer: Int)
  object RoomBasicInfo {
    implicit def RoomBasicInfoCodecJson =
      casecodec3(RoomBasicInfo.apply,RoomBasicInfo.unapply)("name","actualNumberOfPlayer", "maxNumberOfPlayer")
  }

  /**
   * @param basicInfo               Object containing basic information of a room
   * @param scenario                Name of the game map
   * */
  case class RoomInfo(basicInfo: RoomBasicInfo, scenario: String)
  object RoomInfo {
    implicit def RoomInfoCodecJson =
      casecodec2(RoomInfo.apply,RoomInfo.unapply)("basicInfo","scenario")
  }

  case class RoomNameInfo(name: String, players: String)
  object RoomNameInfo {
    implicit def RoomNameInfoCodecJson =
      casecodec2(RoomNameInfo.apply,RoomNameInfo.unapply)("name","players")
  }

  /** Lobby's information
   * @param rooms              The list of name of the rooms
   * @param games              The list of name of the games
   * @param terminatedGames    The list of name of the terminated games
   * */
  case class LobbyInfo(rooms: List[RoomNameInfo], games: List[String], terminatedGames: List[String])
  object LobbyInfo {
    implicit def LobbyInfoCodecJson =
      casecodec3(LobbyInfo.apply,LobbyInfo.unapply)("rooms","games","terminatedGames")
  }

  /*final case class RoomInfoMessage(roomInfo: RoomInfo)

  final case class LobbyInfoMessage(lobbyInfo: LobbyInfo)

  final case class GameInfoMessage()*/

  final case class ErrorMessage(error: String)
  object ErrorMessage {
    implicit def ErrorCodecJson =
      casecodec1(ErrorMessage.apply,ErrorMessage.unapply)("error")
  }
}
