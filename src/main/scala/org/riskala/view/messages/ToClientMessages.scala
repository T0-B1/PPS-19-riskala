package org.riskala.view.messages

import argonaut.Argonaut._
import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.{MapGeography, Player, PlayerState}

import scala.scalajs.js.annotation.JSExportAll

/**
 * Structure of messages that are sent to client
 * */
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

  /**
   * @param objective Personal goal of the player during a game
   * @param cards The list of cards that the player has
   * */
  case class GamePersonalInfo(objective: Objective = Objective(), cards: List[Cards] = List.empty[Cards])
  object GamePersonalInfo {
    implicit def GamePersonalInfoCodecJson =
      casecodec2(GamePersonalInfo.apply, GamePersonalInfo.unapply)("objective", "cards")
  }

  /**
   * @param players List of players into the game
   * @param actualPlayer The player who is playing
   * @param troopsToDeploy The number of troops that the player can deploy
   * @param map The map on which players will play
   * @param playerStates The states that the player owns
   * @param personalInfo Personal info about objective and cards
   * */
  case class GameFullInfo(players:Set[String],
                          actualPlayer:String,
                          troopsToDeploy:Int,
                          map:MapGeography,
                          playerStates: Set[PlayerState],
                          personalInfo:GamePersonalInfo)
  object GameFullInfo {
    implicit def GameFullInfoCodecJson =
      casecodec6(GameFullInfo.apply,GameFullInfo.unapply)("players","actualPlayer","troopsToDeploy","map","playerStates","personalInfo")
  }

  /**
   * @param actualPlayer The new player who is playing
   * @param troopsToDeploy The number of troops that the player can deploy
   * @param personalInfo Personal info about objective and cards
   * */
  case class GameUpdate(actualPlayer:String, troopsToDeploy:Int, playerStates: Set[PlayerState],personalInfo:GamePersonalInfo)
  object GameUpdate {
    implicit def GameUpdateCodecJson =
      casecodec4(GameUpdate.apply,GameUpdate.unapply)("actualPlayer","troopsToDeploy","playerState","personalInfo")
  }

  case class GameEnd(winner: Player)
  object GameEnd {
    implicit def GameEndCodecJson =
      casecodec1(GameEnd.apply,GameEnd.unapply)("winner")
  }

  /**
   * @param error The error occurred
   * */
  final case class ErrorMessage(error: String)
  object ErrorMessage {
    implicit def ErrorCodecJson =
      casecodec1(ErrorMessage.apply,ErrorMessage.unapply)("error")
  }
}
