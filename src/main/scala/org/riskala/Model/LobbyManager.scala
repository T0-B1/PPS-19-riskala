package org.riskala.Model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import ModelMessages._

import scala.collection.immutable.{HashMap, HashSet}

object LobbyManager {

  case class Lobby(rooms: List[String], games: List[String], terminatedGames: List[String])

  private var subscribers: HashSet[ActorRef[PlayerMessage]] = HashSet.empty
  private var rooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)] = HashMap.empty
  private var games: HashMap[String, ActorRef[GameMessage]] = HashMap.empty
  private var terminatedGames: HashMap[String, (ActorRef[GameMessage], Boolean)] = HashMap.empty

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

  def notifyAllSubscribers(): Behavior[LobbyMessage] = {
    subscribers.foreach(s => s ! getInfo())
    Behaviors.same
  }
  
  def apply(): Behavior[LobbyMessage] = Behaviors.receive { (context, message) =>
    message match {
      case Subscribe(subscriber) =>
        subscribers = subscribers + subscriber
        subscriber ! getInfo()
        Behaviors.same

      case CreateRoom(creator, roomInfo) =>
        if (!rooms.contains(roomInfo.basicInfo.name)) {
          //TODO: spawn RoomManager and send Join msg
          val room = context.spawn(Behaviors.ignore[RoomMessage],"RoomManager")
          //room ! Join(creator)
          //TODO: swap null with room
          rooms = rooms + (roomInfo.basicInfo.name -> (room, roomInfo.basicInfo))
          subscribers = subscribers - creator
          println(subscribers)
        } else {
          //TODO: Errore stanza già presente.
          creator ! new PlayerMessage {}
        }
        notifyAllSubscribers()

      case JoinTo(actor, name) =>
        println("received join message")
        if (rooms.contains(name)) {
          println("found room " + name)
          subscribers = subscribers - actor
          rooms.get(name).head._1 ! new RoomMessage {}
        } else {
          //TODO: Errore stanza non trovata.
          println("No room found")
          actor ! new PlayerMessage {}
        }
        println("join message done")

        Behaviors.same

      case StartGame(name, actor) =>
        rooms = rooms - name
        games = games + (name -> actor)
        notifyAllSubscribers()

      case EndGame(name, game) =>
        terminatedGames = terminatedGames + (name -> (game, true))
        games = games - name
        notifyAllSubscribers()

      case GameClosed(name, subs) =>
        terminatedGames = terminatedGames + (name -> (terminatedGames(name)._1, false))
        subscribers = subscribers ++ subs
        notifyAllSubscribers()

      case UpdateRoomInfo(info) =>
        rooms = rooms + (info.name -> (rooms(info.name)._1, info))
        notifyAllSubscribers()

      case EmptyRoom(roomName) =>
        rooms = rooms - roomName
        notifyAllSubscribers()

      case Logout(actor) =>
        subscribers = subscribers - actor
        Behaviors.same
    }
  }

}