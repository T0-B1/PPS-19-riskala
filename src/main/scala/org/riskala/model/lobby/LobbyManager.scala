package org.riskala.model.lobby

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.game.GameManager
import org.riskala.model.game.GameMessages.JoinGame
import org.riskala.model.lobby.LobbyMessages._
import org.riskala.model.room.RoomManager
import org.riskala.model.room.RoomMessages.Join
import org.riskala.view.messages.ToClientMessages.{LobbyInfo, RoomBasicInfo, RoomNameInfo}

object LobbyManager {

  def apply(): Behavior[LobbyMessage] = setupLobbyManager()

  val lobbyServiceKey: ServiceKey[LobbyMessage] = ServiceKey[LobbyMessage]("LobbyManager")

  private def setupLobbyManager(): Behavior[LobbyMessage] = {
    Behaviors.setup { context =>
      context.system.receptionist ! Receptionist.register(lobbyServiceKey, context.self)
      context.log.info("SetupLobbyManager")
      lobbyManager(Set.empty,Map.empty,Map.empty,Map.empty)
    }
  }

  private def lobbyManager(subscribers: Set[ActorRef[PlayerMessage]],
                   rooms: Map[String, (ActorRef[RoomMessage], RoomBasicInfo)],
                   games: Map[String, ActorRef[GameMessage]],
                   terminatedGames: Map[String, (ActorRef[GameMessage], Boolean)]): Behavior[LobbyMessage] =
    Behaviors.receive { (context, message) => {
      
      def nextBehavior(nextSubscribers: Set[ActorRef[PlayerMessage]] = subscribers,
                       nextRooms: Map[String, (ActorRef[RoomMessage], RoomBasicInfo)] = rooms,
                       nextGames: Map[String, ActorRef[GameMessage]] = games,
                       nextTerminatedGames: Map[String, (ActorRef[GameMessage], Boolean)] = terminatedGames
                      ): Behavior[LobbyMessage] = lobbyManager(nextSubscribers,nextRooms,nextGames,nextTerminatedGames)

      def getInfo(nextRooms: Map[String, (ActorRef[RoomMessage], RoomBasicInfo)] = rooms,
                  nextGames: Map[String, ActorRef[GameMessage]] = games,
                  nextTerminatedGames: Map[String, (ActorRef[GameMessage], Boolean)] = terminatedGames
                 ): PlayerMessage = {
        val roomList: Set[RoomNameInfo] = nextRooms.map(kv => RoomNameInfo(kv._1,
          kv._2._2.actualNumberOfPlayer + "/" + kv._2._2.maxNumberOfPlayer)).toSet
        val gameList: Set[String] = nextGames.keys.toSet
        val terminatedGameList: Set[String] = nextTerminatedGames.keys.toSet
        LobbyInfoMessage(LobbyInfo(roomList, gameList, terminatedGameList))
      }

      def notifyAllSubscribers(info: PlayerMessage,
                               subs: Set[ActorRef[PlayerMessage]] = subscribers): Unit = {
        subs.foreach(s => s ! info)
      }

      message match {
        case Subscribe(subscriber) =>
          subscriber ! LobbyReferent(context.self)
          subscriber ! getInfo()
          context.log.info(s"Subscribe from $subscriber")
          println(getInfo())
          nextBehavior(nextSubscribers = subscribers + subscriber)

        case CreateRoom(creator, roomInfo) =>
          if(!games.contains(roomInfo.basicInfo.name) &&
            !terminatedGames.contains(roomInfo.basicInfo.name) &&
            !rooms.contains(roomInfo.basicInfo.name)) {
            val room = context.spawn(RoomManager(roomInfo, context.self),
              "RoomManager-" + roomInfo.basicInfo.name)
            room ! Join(creator)
            val newRooms = rooms + (roomInfo.basicInfo.name -> (room, roomInfo.basicInfo))
            val newSubs = subscribers - creator
            notifyAllSubscribers(getInfo(nextRooms = newRooms),newSubs)
            nextBehavior(nextSubscribers = newSubs,nextRooms = newRooms)
          } else {
            creator ! ErrorMessage("Room already exists")
            nextBehavior()
          }

        case JoinTo(actor, name) =>
          if(rooms.contains(name) || games.contains(name) || terminatedGames.contains(name)){
            val newSubs = subscribers - actor
            rooms.get(name).foreach(_._1 ! Join(actor))
            games.get(name).foreach(_ ! JoinGame(actor))
            terminatedGames.get(name).filter(_._2).foreach(_._1 ! JoinGame(actor))
            nextBehavior(nextSubscribers = newSubs)
          } else {
            actor ! ErrorMessage("Room not found")
            nextBehavior()
          }

        case StartGame(info, players, roomSubscribers) =>
          val newRooms = rooms - info.basicInfo.name
          val gameSub = roomSubscribers++players.values
          val game = context.spawn(GameManager(info.basicInfo.name, gameSub,
            players.keySet, info.scenario, context.self), "GameManager-"+info.basicInfo.name)
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
