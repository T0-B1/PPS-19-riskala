package org.riskala.Model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import ModelMessages._

import scala.collection.immutable.HashMap

object LobbyManager {

  case class Lobby(rooms: List[String], games: List[String], terminatedGames: List[String])

  var subscribers: Set[ActorRef[PlayerMessage]] = Set.empty
  var rooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)] = HashMap.empty
  var games: HashMap[String, ActorRef[GameMessage]] = HashMap.empty
  var terminatedGames: HashMap[String, (ActorRef[GameMessage], Boolean)] = HashMap.empty

  def getInfo(): PlayerMessage = {

    val roomList: List[String] = rooms.map(kv => kv._1 + " "
      + kv._2._2.actualNumberOfPlayer
      + " / " + kv._2._2.maxNumberOfPlayer).toList
    val gameList: List[String] = games.keys.toList
    val terminatedGameList: List[String] = terminatedGames.keys.toList
    Lobby(roomList, gameList, terminatedGameList)

    //TODO: return playerMessage
    new PlayerMessage {}
  }

  def apply(): Behavior[LobbyMessage] = Behaviors.receive { (context, message) =>
    message match {
      case Subscribe(subscriber) =>
        subscribers = subscribers + subscriber
        subscriber ! getInfo()
        Behaviors.same

      case CreateRoom(creator, roomInfo) => ???

      case JoinTo(actor, name) => ???

      case StartGame(name, actor) => ???

      case EndGame(name, game) =>
        terminatedGames = terminatedGames + (name -> (game, true))
        games = games - name
        Behaviors.same

      case GameClosed(name, subscribers) => ???

      case UpdateRoomInfo(info) =>
        rooms = rooms + (info.name -> (rooms(info.name)._1, info))
        Behaviors.same

      case EmptyRoom(roomName) =>
        rooms = rooms - roomName
        Behaviors.same

      case Logout(actor) =>
        subscribers = subscribers - actor
        Behaviors.same
    }
  }

}
