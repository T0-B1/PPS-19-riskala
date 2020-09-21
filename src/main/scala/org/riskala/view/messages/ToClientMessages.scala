package org.riskala.view.messages

import argonaut.Argonaut._
import argonaut.CodecJson
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
    implicit def RoomBasicInfoCodecJson: CodecJson[RoomBasicInfo] =
      casecodec3(RoomBasicInfo.apply,RoomBasicInfo.unapply)("name","actualNumberOfPlayer", "maxNumberOfPlayer")
  }

  /**
   * @param basicInfo               Object containing basic information of a room
   * @param scenario                Name of the game map
   * */
  case class RoomInfo(basicInfo: RoomBasicInfo, players: Set[Player], scenario: String)
  object RoomInfo {
    implicit def RoomInfoCodecJson: CodecJson[RoomInfo] =
      casecodec3(RoomInfo.apply,RoomInfo.unapply)("basicInfo","players","scenario")
  }

  /**
   *@param name The name of the room
   *@param players The name of the player
   * */
  case class RoomNameInfo(name: String, players: String)
  object RoomNameInfo {
    implicit def RoomNameInfoCodecJson: CodecJson[RoomNameInfo] =
      casecodec2(RoomNameInfo.apply,RoomNameInfo.unapply)("name","players")
  }

  /**
   * @param rooms              The list of name of the rooms
   * @param games              The list of name of the games
   * @param terminatedGames    The list of name of the terminated games
   * */
  case class LobbyInfo(rooms: Set[RoomNameInfo], games: Set[String], terminatedGames: Set[String])
  object LobbyInfo {
    implicit def LobbyInfoCodecJson: CodecJson[LobbyInfo] =
      casecodec3(LobbyInfo.apply,LobbyInfo.unapply)("rooms","games","terminatedGames")
  }

  /**
   * Information strictly related to a specific player
   * @param objective Personal goal of the player during a game
   * @param cards The list of cards that the player has
   * */
  case class GamePersonalInfo(objective: Objective = Objective(), cards: List[Cards] = List.empty[Cards])
  object GamePersonalInfo {
    implicit def GamePersonalInfoCodecJson: CodecJson[GamePersonalInfo] =
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
                          isDeployOnly: Boolean,
                          map:MapGeography,
                          playerStates: Set[PlayerState],
                          personalInfo:GamePersonalInfo,
                          winner: Option[Player])
  object GameFullInfo {
    implicit def GameFullInfoCodecJson: CodecJson[GameFullInfo] =
      casecodec8(GameFullInfo.apply,GameFullInfo.unapply)("players","actualPlayer","troopsToDeploy","isDeployOnly","map","playerStates","personalInfo","winner")
  }

  /**
   * @param actualPlayer The new player who is playing
   * @param troopsToDeploy The number of troops that the player can deploy
   * @param personalInfo Personal info about objective and cards
   * */
  case class GameUpdate(actualPlayer:String, troopsToDeploy:Int, isDeployOnly: Boolean, playerStates: Set[PlayerState],personalInfo:GamePersonalInfo)
  object GameUpdate {
    implicit def GameUpdateCodecJson: CodecJson[GameUpdate] =
      casecodec5(GameUpdate.apply,GameUpdate.unapply)("actualPlayer","troopsToDeploy","isDeployOnly","playerState","personalInfo")
  }

  /**
   * @param winner The player who won the game
   * */
  case class GameEnd(winner: Player)
  object GameEnd {
    implicit def GameEndCodecJson: CodecJson[GameEnd] =
      casecodec1(GameEnd.apply,GameEnd.unapply)("winner")
  }

  /**
   * @param error The error occurred
   * */
  final case class ErrorMessage(error: String)
  object ErrorMessage {
    implicit def ErrorCodecJson: CodecJson[ErrorMessage] =
      casecodec1(ErrorMessage.apply,ErrorMessage.unapply)("error")
  }
}
