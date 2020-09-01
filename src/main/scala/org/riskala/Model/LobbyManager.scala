package org.riskala.Model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import ModelMessages._
import akka.actor.ActorRef

import scala.collection.immutable.HashMap

object LobbyManager {

  case class Lobby(rooms: List[String], games: List[String], terminatedGames: List[String])

  var subscribers: Set[ActorRef] = Set.empty
  var rooms: HashMap[String, (ActorRef, RoomBasicInfo)] = HashMap.empty
  var games: HashMap[String, ActorRef] = HashMap.empty
  var terminatedGames: HashMap[String, (ActorRef, Boolean)] = HashMap.empty

  def apply(): Behavior[LobbyMessage] = Behaviors.receive { (context, message) =>
    message match {
      case Subscribe(subscriber) => ???

      case CreateRoom(creator, roomInfo) => ???

      case JoinTo(actor, name) => ???

      case StartGame(name, actor) => ???

      case EndGame(name, game) => ???

      case GameClosed(name, subscribers) => ???

      case UpdateRoomInfo(info) => ???

      case EmptyRoom(roomName) => ???

      case Logout(actor) => ???
    }
  }
}
