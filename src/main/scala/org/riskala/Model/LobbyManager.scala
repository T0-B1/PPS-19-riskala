package org.riskala.Model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import ModelMessages._

import scala.collection.immutable.{HashMap, HashSet}

object LobbyManager {

  case class Lobby(rooms: List[String], games: List[String], terminatedGames: List[String])

  def apply(): Behavior[LobbyMessage] = lobbyManager(HashSet.empty,HashMap.empty,HashMap.empty,HashMap.empty)

  private def lobbyManager(subscribers: HashSet[ActorRef[PlayerMessage]],
                   rooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)],
                   games: HashMap[String, ActorRef[GameMessage]],
                   terminatedGames: HashMap[String, (ActorRef[GameMessage], Boolean)]): Behavior[LobbyMessage] =
    Behaviors.receive { (context, message) => {

      def nextBehavior(nextSubscribers: HashSet[ActorRef[PlayerMessage]] = subscribers,
                       nextRooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)] = rooms,
                       nextGames: HashMap[String, ActorRef[GameMessage]] = games,
                       nextTerminatedGames: HashMap[String, (ActorRef[GameMessage], Boolean)] = terminatedGames
                      ): Behavior[LobbyMessage] = lobbyManager(nextSubscribers,nextRooms,nextGames,nextTerminatedGames)

      def getInfo(nextRooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)] = rooms,
                  nextGames: HashMap[String, ActorRef[GameMessage]] = games,
                  nextTerminatedGames: HashMap[String, (ActorRef[GameMessage], Boolean)] = terminatedGames
                 ): PlayerMessage = {
        val roomList: List[String] = nextRooms.map(kv => kv._1 + " "
          + kv._2._2.actualNumberOfPlayer
          + " / " + kv._2._2.maxNumberOfPlayer).toList
        val gameList: List[String] = nextGames.keys.toList
        val terminatedGameList: List[String] = nextTerminatedGames.keys.toList
        Lobby(roomList, gameList, terminatedGameList)
        println("GOT-INFO")
        //TODO: return playerMessage
        new PlayerMessage {}
      }

      def notifyAllSubscribers(info: PlayerMessage,
                               subs: HashSet[ActorRef[PlayerMessage]] = subscribers): Unit = {
        subs.foreach(s => {
          println("UPDATING " + s)
          s ! info
        })
        println("NOTIFIED-ALL")
      }

      message match {
        case Subscribe(subscriber) =>
          val newSubs = subscribers + subscriber
          subscriber ! getInfo()
          println("SUB-MSG Subs: " + newSubs)
          //lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = terminatedGames)
          nextBehavior(nextSubscribers = newSubs)

        case CreateRoom(creator, roomInfo) =>
          var newRooms: HashMap[String, (ActorRef[RoomMessage], RoomBasicInfo)] = rooms
          var newSubs: HashSet[ActorRef[PlayerMessage]] = subscribers
          if (!rooms.contains(roomInfo.basicInfo.name)) {
            //TODO: spawn RoomManager and send Join msg
            val room = context.spawn(Behaviors.ignore[RoomMessage], "RoomManager" + roomInfo.basicInfo.name)
            //room ! Join(creator)
            room ! new RoomMessage {}
            //TODO: swap null with room
            newRooms = rooms + (roomInfo.basicInfo.name -> (room, roomInfo.basicInfo))
            newSubs = subscribers - creator
            println("SUBS: " + newSubs)
          } else {
            //TODO: Errore stanza già presente.
            creator ! new PlayerMessage {}
          }
          println("CREATE DONE, TIME TO NOTIFY ALL")
          notifyAllSubscribers(getInfo(nextRooms = newRooms),newSubs)
          //lobbyManager(subscribers = newSubs,rooms = newRooms,games = games,terminatedGames = terminatedGames)
          nextBehavior(nextSubscribers = newSubs,nextRooms = newRooms)

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
          //lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = terminatedGames)
          nextBehavior(nextSubscribers = newSubs)

        case StartGame(name, actor) =>
          val newRooms = rooms - name
          val newGames = games + (name -> actor)
          notifyAllSubscribers(getInfo(nextRooms = newRooms,nextGames = newGames))
          //lobbyManager(subscribers = subscribers,rooms = newRooms,games = newGames,terminatedGames = terminatedGames)
          nextBehavior(nextRooms = newRooms,nextGames = newGames)

        case EndGame(name, game) =>
          val newTerminatedGames = terminatedGames + (name -> (game, true))
          val newGames = games - name
          notifyAllSubscribers(getInfo(nextGames = newGames,nextTerminatedGames = newTerminatedGames))
          //lobbyManager(subscribers = subscribers,rooms = rooms,games = newGames,terminatedGames = newTerminatedGames)
          nextBehavior(nextGames = newGames,nextTerminatedGames = newTerminatedGames)

        case GameClosed(name, subs) =>
          val newTerminatedGames = terminatedGames + (name -> (terminatedGames(name)._1, false))
          val newSubs = subscribers ++ subs
          notifyAllSubscribers(getInfo(nextTerminatedGames = newTerminatedGames),newSubs)
          //lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = newTerminatedGames)
          nextBehavior(nextSubscribers = newSubs,nextTerminatedGames = newTerminatedGames)

        case UpdateRoomInfo(info) =>
          val newRooms = rooms + (info.name -> (rooms(info.name)._1, info))
          notifyAllSubscribers(getInfo(nextRooms = newRooms))
          //lobbyManager(subscribers = subscribers,rooms = newRooms,games = games,terminatedGames = terminatedGames)
          nextBehavior(nextRooms = newRooms)

        case EmptyRoom(roomName) =>
          val newRooms = rooms - roomName
          notifyAllSubscribers(getInfo(nextRooms = newRooms))
          //lobbyManager(subscribers = subscribers,rooms = newRooms,games = games,terminatedGames = terminatedGames)
          nextBehavior(nextRooms = newRooms)

        case Logout(actor) =>
          val newSubs = subscribers - actor
          println("LOGOUT unSub:" + actor + " SubList:" + newSubs)
          //lobbyManager(subscribers = newSubs,rooms = rooms,games = games,terminatedGames = terminatedGames)
          nextBehavior(nextSubscribers = newSubs)
      }
    }
  }

}
