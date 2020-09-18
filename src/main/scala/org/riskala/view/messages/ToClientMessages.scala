package org.riskala.view.messages

import argonaut.Argonaut._
import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.{MapGeo, Player, PlayerState}

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
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
  case class RoomInfo(basicInfo: RoomBasicInfo, players: Set[Player], scenario: String)
  object RoomInfo {
    implicit def RoomInfoCodecJson =
      casecodec3(RoomInfo.apply,RoomInfo.unapply)("basicInfo","players","scenario")
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
  case class LobbyInfo(rooms: Set[RoomNameInfo], games: Set[String], terminatedGames: Set[String])
  object LobbyInfo {
    implicit def LobbyInfoCodecJson =
      casecodec3(LobbyInfo.apply,LobbyInfo.unapply)("rooms","games","terminatedGames")
  }

  case class GamePersonalInfo(objective: Objective, cards: List[Cards])
  object GamePersonalInfo {
    implicit def GamePersonalInfoCodecJson =
      casecodec2(GamePersonalInfo.apply, GamePersonalInfo.unapply)("objective", "cards")
  }

  case class GameFullInfo(players:Set[String],
                          actualPlayer:String,
                          troopsToDeploy:Int,
                          map:MapGeo,
                          playerStates: Set[PlayerState],
                          personalInfo:GamePersonalInfo)
  object GameFullInfo {
    implicit def GameFullInfoCodecJson =
      casecodec6(GameFullInfo.apply,GameFullInfo.unapply)("players","actualPlayer","troopsToDeploy","map","playerStates","personalInfo")
  }

  case class GameUpdate(actualPlayer:String, troopsToDeploy:Int, playerStates: Set[PlayerState],personalInfo:GamePersonalInfo)
  object GameUpdate {
    implicit def GameUpdateCodecJson =
      casecodec4(GameUpdate.apply,GameUpdate.unapply)("actualPlayer","troopsToDeploy","playerState","personalInfo")
  }

  final case class ErrorMessage(error: String)
  object ErrorMessage {
    implicit def ErrorCodecJson =
      casecodec1(ErrorMessage.apply,ErrorMessage.unapply)("error")
  }
}
