package org.riskala.model.room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.game.GameManager
import org.riskala.model.lobby.LobbyMessages.{EmptyRoom, StartGame, Subscribe, UpdateRoomInfo}
import scala.collection.immutable.{HashMap, HashSet}
import monocle.Lens
import monocle.macros.GenLens

object RoomManager {

  def apply(roomInfo: RoomInfo, lobby: ActorRef[LobbyMessage]): Behavior[RoomMessage] =
    roomManager(HashSet.empty, HashMap.empty, roomInfo, lobby)

  def roomManager(subscribersRoom: HashSet[ActorRef[PlayerMessage]],
                  readyPlayerMap: HashMap[String,ActorRef[PlayerMessage]],
                  roomInfo: RoomInfo,
                  lobby: ActorRef[LobbyMessage]
                 ):Behavior[RoomMessage] = {
    Behaviors.receive { (context, message) =>

      val lensBasicInfo: Lens[RoomInfo,RoomBasicInfo] = GenLens[RoomInfo](_.basicInfo)
      val lensActualNumber: Lens[RoomBasicInfo, Int] = GenLens[RoomBasicInfo](_.actualNumberOfPlayer)

      def decreaseActualPlayer(ri:RoomInfo):RoomInfo = (lensBasicInfo composeLens lensActualNumber).modify(_ - 1)(ri)
      def increaseActualPlayer(ri:RoomInfo):RoomInfo = (lensBasicInfo composeLens lensActualNumber).modify(_ + 1)(ri)

      def notifyUpdateRoomInfo(newSubscribers: HashSet[ActorRef[PlayerMessage]],
                               newReady: HashMap[String,ActorRef[PlayerMessage]],
                               newRoomInfo: RoomInfo): Unit = {
        newReady.foreach(rp => rp._2 ! RoomInfoMessage(newRoomInfo))
        newSubscribers. foreach(s => s ! RoomInfoMessage(newRoomInfo))
        lobby ! UpdateRoomInfo(newRoomInfo.basicInfo)
      }

      def updateBehavior(updatedSub: HashSet[ActorRef[PlayerMessage]] = subscribersRoom,
                         updatedReady: HashMap[String,ActorRef[PlayerMessage]] = readyPlayerMap,
                         updatedRoomInfo: RoomInfo = roomInfo,
                         updatedLobby: ActorRef[LobbyMessage] = lobby
                        ):Behavior[RoomMessage] = {
        context.log.info("updSub: -> "+ updatedSub.toList)
        context.log.info("updReady: -> "+ updatedReady.toList)
        context.log.info("updRI: -> "+ updatedRoomInfo)
        context.log.info("updLobby: -> "+ updatedLobby)
        context.log.info( "------- --------")
        roomManager(updatedSub, updatedReady, updatedRoomInfo, updatedLobby)
      }

      message match {
        case Join(actor) =>
          context.log.info("JOIN")
          val newSubscriber = subscribersRoom + actor
          context.log.info("After SUB "+newSubscriber.size)
          actor ! RoomInfoMessage(roomInfo)
          updateBehavior(updatedSub = newSubscriber)

        case Leave(actor) =>
          context.log.info("LEAVE")
          var newReady = readyPlayerMap
          var newSubscribers = subscribersRoom
          var newRoomInfo = roomInfo
          if(readyPlayerMap.toList.exists(kv => kv._2 == actor)){
            newReady = readyPlayerMap.filter(kv => kv._2 != actor)
            newRoomInfo = decreaseActualPlayer(roomInfo)
            notifyUpdateRoomInfo(subscribersRoom, newReady, newRoomInfo)
          } else {
            newSubscribers = subscribersRoom - actor
          }
          lobby ! Subscribe(actor)
          if(newSubscribers.isEmpty && newReady.isEmpty){
            lobby ! EmptyRoom(roomInfo.basicInfo.name)
            context.log.info("LeaveMessage!- Behavior stopped")
            Behaviors.stopped
          } else {
            updateBehavior(updatedSub = newSubscribers, updatedReady = newReady, updatedRoomInfo = newRoomInfo)
          }

        case UnReady(playerName, actor) =>
          context.log.info("UNREADY")
          val newReady = readyPlayerMap - playerName
          //Update actualNumberPlayer
          val newRoomInfo = decreaseActualPlayer(roomInfo)
          val newSubscriber = subscribersRoom + actor
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Ready(playerName, actor) =>
          context.log.info("READY")
          //Update actualNumberPlayer
          val newRoomInfo = increaseActualPlayer(roomInfo)
          context.log.info("newRoomInfo - "+ newRoomInfo)
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          //Add the actor into readyPlayerList
          val newReady = readyPlayerMap + (playerName -> actor)

          if (newReady.size == newRoomInfo.basicInfo.maxNumberOfPlayer) {
            //Game can start
            lobby ! StartGame(roomInfo, newReady, newSubscriber)
            //TODO: Change behavior from Room to Game -> GameManager()
            context.spawn(GameManager(), "GameManager")
            context.log.info("Ready room Stopped")
            Behaviors.stopped
          } else {
            notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
            updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)
          }

        case Logout(actor) =>
          context.log.info("LOGOUT")
          var newReady = readyPlayerMap
          var newSubscribers = subscribersRoom
          var newRoomInfo = roomInfo
          if(readyPlayerMap.toList.exists(kv => kv._2 == actor)){
            newReady = readyPlayerMap.filter(kv => kv._2 != actor)
            newRoomInfo = decreaseActualPlayer(roomInfo)
            notifyUpdateRoomInfo(newSubscribers, newReady, newRoomInfo)
          } else {
            newSubscribers = subscribersRoom - actor
          }
          if(newSubscribers.isEmpty && newReady.isEmpty){
            lobby ! EmptyRoom(roomInfo.basicInfo.name)
            context.log.info("LogoutMessage! - Behavior stopped")
            Behaviors.stopped
          } else {
            updateBehavior(updatedSub = newSubscribers, updatedReady = newReady, updatedRoomInfo = newRoomInfo)
          }
      }
    }
  }
}
