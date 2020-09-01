package org.riskala.Model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import ModelMessages._

import scala.collection.immutable.{HashMap, HashSet}

object LobbyManager {

  case class Lobby(rooms: List[String], games: List[String], terminatedGames: List[String])

  /*
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
    println("GETINFO")
    //TODO: return playerMessage
    new PlayerMessage {}
  }

  def notifyAllSubscribers(): Behavior[LobbyMessage] = {
    subscribers.foreach(s => {
      println("UPDATING " + s)
      s ! getInfo()
    })
    println("NOTIFYALL")
    Behaviors.same
  }
  */

  def apply(): Behavior[LobbyMessage] = lobbyManager(HashSet.empty,HashMap.empty,HashMap.empty,HashMap.empty)

  def lobbyManager(subscribers: HashSet[ActorRef[PlayerMessage]],
                   rooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)],
                   games: HashMap[String, ActorRef[GameMessage]],
                   terminatedGames: HashMap[String, (ActorRef[GameMessage], Boolean)]): Behavior[LobbyMessage] =
    Behaviors.receive { (context, message) => {

      def getInfo(): PlayerMessage = {
        val roomList: List[String] = rooms.map(kv => kv._1 + " "
          + kv._2._2.actualNumberOfPlayer
          + " / " + kv._2._2.maxNumberOfPlayer).toList
        val gameList: List[String] = games.keys.toList
        val terminatedGameList: List[String] = terminatedGames.keys.toList
        Lobby(roomList, gameList, terminatedGameList)
        println("GETINFO")
        //TODO: return playerMessage
        new PlayerMessage {}
      }

      def notifyAllSubscribers(): Unit = {
        subscribers.foreach(s => {
          println("UPDATING " + s)
          s ! getInfo()
        })
        println("NOTIFYALL")
      }

      message match {
        case Subscribe(subscriber) =>
          val newSubs = subscribers + subscriber
          subscriber ! getInfo()
          println("SUB-MSG Sub:" + subscriber + " SubList:" + subscribers)
          lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = terminatedGames)

        case CreateRoom(creator, roomInfo) =>
          var newRooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)] = rooms
          var newSubs: HashSet[ActorRef[PlayerMessage]] = subscribers
          if (!rooms.contains(roomInfo.basicInfo.name)) {
            //TODO: spawn RoomManager and send Join msg
            val room = context.spawn(Behaviors.ignore[RoomMessage], "RoomManager")
            //room ! Join(creator)
            //TODO: swap null with room
            newRooms = rooms + (roomInfo.basicInfo.name -> (room, roomInfo.basicInfo))
            newSubs = subscribers - creator
            println("SUBS: " + subscribers)
          } else {
            //TODO: Errore stanza già presente.
            creator ! new PlayerMessage {}
          }
          println("CREATE DONE, NOTIFYALL")
          notifyAllSubscribers()
          lobbyManager(subscribers = newSubs,rooms = newRooms,games = games,terminatedGames = terminatedGames)

        case JoinTo(actor, name) =>
          println("received join message")
          var newSubs = subscribers
          if (rooms.contains(name)) {
            println("found room " + name)
            newSubs = subscribers - actor
            rooms.get(name).head._1 ! new RoomMessage {}
          } else {
            //TODO: Errore stanza non trovata.
            println("No room found")
            actor ! new PlayerMessage {}
          }
          println("join message done")

          lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = terminatedGames)

        case StartGame(name, actor) =>
          val newRooms = rooms - name
          val newGames = games + (name -> actor)
          notifyAllSubscribers()
          lobbyManager(subscribers = subscribers,rooms = newRooms,games = newGames,terminatedGames = terminatedGames)

        case EndGame(name, game) =>
          val newTerminatedGames = terminatedGames + (name -> (game, true))
          val newGames = games - name
          notifyAllSubscribers()
          lobbyManager(subscribers = subscribers,rooms = rooms,games = newGames,terminatedGames = newTerminatedGames)

        case GameClosed(name, subs) =>
          val newTerminatedGames = terminatedGames + (name -> (terminatedGames(name)._1, false))
          val newSubs = subscribers ++ subs
          notifyAllSubscribers()
          lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = newTerminatedGames)

        case UpdateRoomInfo(info) =>
          val newRooms = rooms + (info.name -> (rooms(info.name)._1, info))
          notifyAllSubscribers()
          lobbyManager(subscribers = subscribers,rooms = newRooms,games = games,terminatedGames = terminatedGames)

        case EmptyRoom(roomName) =>
          val newRooms = rooms - roomName
          notifyAllSubscribers()
          lobbyManager(subscribers = subscribers,rooms = newRooms,games = games,terminatedGames = terminatedGames)

        case Logout(actor) =>
          val newSubs = subscribers - actor
          println("LOGOUT unSub:" + actor + " SubList:" + subscribers)
          lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = terminatedGames)
      }
    }
  }

}
