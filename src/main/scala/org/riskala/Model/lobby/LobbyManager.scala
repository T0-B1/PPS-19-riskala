package org.riskala.model.lobby

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.riskala.controller.actors.PlayerMessages.{PlayerMessage, RoomAlreadyExistsMessage, RoomNotFoundMessage}
import org.riskala.model.ModelMessages._
import org.riskala.model.game.GameManager
import org.riskala.model.lobby.LobbyMessages._
import org.riskala.model.room.RoomManager
import org.riskala.model.room.RoomMessages.{Join, RoomBasicInfo}

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
            val room = context.spawn(RoomManager(roomInfo, context.self),
              "RoomManager-" + roomInfo.basicInfo.name)
            room ! Join(creator)
            val newRooms = rooms + (roomInfo.basicInfo.name -> (room, roomInfo.basicInfo))
            val newSubs = subscribers - creator
            notifyAllSubscribers(getInfo(nextRooms = newRooms),newSubs)
            nextBehavior(nextSubscribers = newSubs,nextRooms = newRooms)
          } else {
            creator ! RoomAlreadyExistsMessage()
            notifyAllSubscribers(getInfo())
            nextBehavior()
          }

        case JoinTo(actor, name) =>
          if (rooms.contains(name)) {
            val newSubs = subscribers - actor
            rooms.get(name).head._1 ! Join(actor)
            nextBehavior(nextSubscribers = newSubs)
          } else {
            actor ! RoomNotFoundMessage()
            nextBehavior()
          }
          
        case StartGame(info, players, roomSubscribers) =>
          val newRooms = rooms - info.basicInfo.name
          //TODO: pass info to GM (roomInfo + subscribers+ players)
          val game = context.spawn(GameManager(), "GameManager-"+info.basicInfo.name)
          val newGames = games + (info.basicInfo.name -> game)
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
