package org.riskala.model.lobby

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import LobbyMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.room.RoomMessages.RoomBasicInfo

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
        //TODO: return playerMessage
        new PlayerMessage {}
      }

      def notifyAllSubscribers(info: PlayerMessage,
                               subs: HashSet[ActorRef[PlayerMessage]] = subscribers): Unit = {
        subs.foreach(s => s ! info)
      }

      message match {
        case Subscribe(subscriber) =>
          subscriber ! getInfo()
          nextBehavior(nextSubscribers = subscribers + subscriber)

        case CreateRoom(creator, roomInfo) =>
          if (!rooms.contains(roomInfo.basicInfo.name)) {
            //TODO: spawn RoomManager and send Join msg
            val room = context.spawn(Behaviors.ignore[RoomMessage], "RoomManager" + roomInfo.basicInfo.name)
            //room ! Join(creator)
            room ! new RoomMessage {}
            //TODO: swap null with room
            val newRooms = rooms + (roomInfo.basicInfo.name -> (room, roomInfo.basicInfo))
            val newSubs = subscribers - creator
            notifyAllSubscribers(getInfo(nextRooms = newRooms),newSubs)
            nextBehavior(nextSubscribers = newSubs,nextRooms = newRooms)
          } else {
            //TODO: Errore stanza già presente.
            creator ! new PlayerMessage {}
            notifyAllSubscribers(getInfo())
            nextBehavior()
          }

        case JoinTo(actor, name) =>
          if (rooms.contains(name)) {
            val newSubs = subscribers - actor
            rooms.get(name).head._1 ! new RoomMessage {}
            nextBehavior(nextSubscribers = newSubs)
          } else {
            //TODO: Errore stanza non trovata.
            actor ! new PlayerMessage {}
            nextBehavior()
          }
          
        case StartGame(name, actor) =>
          val newRooms = rooms - name
          val newGames = games + (name -> actor)
          notifyAllSubscribers(getInfo(nextRooms = newRooms,nextGames = newGames))
          nextBehavior(nextRooms = newRooms,nextGames = newGames)

        case EndGame(name, game) =>
          val newTerminatedGames = terminatedGames + (name -> (game, true))
          val newGames = games - name
          notifyAllSubscribers(getInfo(nextGames = newGames,nextTerminatedGames = newTerminatedGames))
          nextBehavior(nextGames = newGames,nextTerminatedGames = newTerminatedGames)

        case GameClosed(name, subs) =>
          val newTerminatedGames = terminatedGames + (name -> (terminatedGames(name)._1, false))
          val newSubs = subscribers ++ subs
          notifyAllSubscribers(getInfo(nextTerminatedGames = newTerminatedGames),newSubs)
          nextBehavior(nextSubscribers = newSubs,nextTerminatedGames = newTerminatedGames)

        case UpdateRoomInfo(info) =>
          val newRooms = rooms + (info.name -> (rooms(info.name)._1, info))
          notifyAllSubscribers(getInfo(nextRooms = newRooms))
          nextBehavior(nextRooms = newRooms)

        case EmptyRoom(roomName) =>
          val newRooms = rooms - roomName
          notifyAllSubscribers(getInfo(nextRooms = newRooms))
          nextBehavior(nextRooms = newRooms)

        case Logout(actor) => nextBehavior(nextSubscribers = subscribers - actor)
      }
    }
  }

}
