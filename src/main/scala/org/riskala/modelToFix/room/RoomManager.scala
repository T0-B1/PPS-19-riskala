package org.riskala.modelToFix.room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.modelToFix.ModelMessages._
import org.riskala.modelToFix.lobby.LobbyMessages.{EmptyRoom, StartGame, Subscribe, UpdateRoomInfo}

import scala.collection.immutable.{HashMap, HashSet}
import monocle.Lens
import monocle.macros.GenLens
import org.riskala.modelToFix.ModelMessages.{LobbyMessage, RoomMessage}
import org.riskala.modelToFix.Player
import org.riskala.view.messages.ToClientMessages.{RoomBasicInfo, RoomInfo}

object RoomManager {

  /**
   * Creates a new room behavior
   * @param roomInfo    Information for this room
   * @param lobby       Actor ref of the lobby
   * @return A new Behavior[RoomMessage]
   * */
  def apply(roomInfo: RoomInfo, lobby: ActorRef[LobbyMessage]): Behavior[RoomMessage] =
    roomManager(Set.empty, Map.empty, roomInfo, lobby)

  private def roomManager(subscribersRoom: Set[ActorRef[PlayerMessage]],
                  readyPlayerMap: Map[Player,ActorRef[PlayerMessage]],
                  roomInfo: RoomInfo,
                  lobby: ActorRef[LobbyMessage]
                 ):Behavior[RoomMessage] = {
    Behaviors.receive { (context, message) =>

      val lensBasicInfo: Lens[RoomInfo,RoomBasicInfo] = GenLens[RoomInfo](_.basicInfo)
      val lensActualPlayers: Lens[RoomInfo,Set[Player]] = GenLens[RoomInfo](_.players)
      val lensActualNumber: Lens[RoomBasicInfo, Int] = GenLens[RoomBasicInfo](_.actualNumberOfPlayer)

      def decreaseActualPlayer(ri:RoomInfo,re:Set[Player]):RoomInfo =
        readyUpdater((lensBasicInfo composeLens lensActualNumber).modify(_ - 1)(ri),re)
      def increaseActualPlayer(ri:RoomInfo,re:Set[Player]):RoomInfo =
        readyUpdater((lensBasicInfo composeLens lensActualNumber).modify(_ + 1)(ri),re)
      def readyUpdater(ri:RoomInfo,re:Set[Player]):RoomInfo = lensActualPlayers.modify(_ => re)(ri)

      def notifyUpdateRoomInfo(newSubscribers: Set[ActorRef[PlayerMessage]],
                               newReady: Map[Player,ActorRef[PlayerMessage]],
                               newRoomInfo: RoomInfo): Unit = {
        newReady.foreach(rp => rp._2 ! RoomInfoMessage(newRoomInfo))
        newSubscribers. foreach(s => s ! RoomInfoMessage(newRoomInfo))
        lobby ! UpdateRoomInfo(newRoomInfo.basicInfo)
      }

      def updateBehavior(updatedSub: Set[ActorRef[PlayerMessage]] = subscribersRoom,
                         updatedReady: Map[Player,ActorRef[PlayerMessage]] = readyPlayerMap,
                         updatedRoomInfo: RoomInfo = roomInfo,
                         updatedLobby: ActorRef[LobbyMessage] = lobby
                        ):Behavior[RoomMessage] = {
        context.log.info("Room updated subscribers: -> "+ updatedSub.toList)
        context.log.info("Room updated ready player: -> "+ updatedReady.toList)
        context.log.info("Room updated room info: -> "+ updatedRoomInfo)
        context.log.info( "------- --------")
        roomManager(updatedSub, updatedReady, updatedRoomInfo, updatedLobby)
      }

      message match {
        case Join(actor) =>
          actor ! RoomReferent(context.self)
          context.log.info("Room received JOIN message")
          val newSubscriber = subscribersRoom + actor
          context.log.info("Number of subscribers after subscriprion into the room "+newSubscriber.size)
          actor ! RoomInfoMessage(roomInfo)
          updateBehavior(updatedSub = newSubscriber)

        case Leave(actor) =>
          context.log.info("Room received LEAVE message")
          var newReady = readyPlayerMap
          var newSubscribers = subscribersRoom
          var newRoomInfo = roomInfo
          if(readyPlayerMap.toList.exists(kv => kv._2 == actor)){
            newReady = readyPlayerMap.filter(kv => kv._2 != actor)
            newRoomInfo = decreaseActualPlayer(roomInfo,newReady.keySet)
            notifyUpdateRoomInfo(subscribersRoom, newReady, newRoomInfo)
          } else {
            newSubscribers = subscribersRoom - actor
          }
          lobby ! Subscribe(actor)
          if(newSubscribers.isEmpty && newReady.isEmpty){
            lobby ! EmptyRoom(roomInfo.basicInfo.name)
            context.log.info("Everybody leaved the room - Behavior stopped")
            Behaviors.stopped
          } else {
            updateBehavior(updatedSub = newSubscribers, updatedReady = newReady, updatedRoomInfo = newRoomInfo)
          }

        case UnReady(playerName, actor) =>
          context.log.info("Room received UNREADY message")
          val newReady = readyPlayerMap - Player(playerName,"")
          val newRoomInfo = decreaseActualPlayer(roomInfo,newReady.keySet)
          val newSubscriber = subscribersRoom + actor
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Ready(player, actor) =>
          context.log.info("Room received READY message")
          val newSubscriber = subscribersRoom - actor
          val newReady = readyPlayerMap + (player -> actor)
          val newRoomInfo = increaseActualPlayer(roomInfo,newReady.keySet)
          context.log.info("Updated newRoomInfo - "+ newRoomInfo)
          if (newReady.size == newRoomInfo.basicInfo.maxNumberOfPlayer) {
            lobby ! StartGame(newRoomInfo, newReady, newSubscriber)
            context.log.info("Everybody into the room are ready. Start game and Room behavior stopped")
            Behaviors.stopped
          } else {
            notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
            updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)
          }

        case Logout(actor) =>
          context.log.info("Room received LOGOUT message")
          var newReady = readyPlayerMap
          var newSubscribers = subscribersRoom
          var newRoomInfo = roomInfo
          if(readyPlayerMap.toList.exists(kv => kv._2 == actor)){
            newReady = readyPlayerMap.filter(kv => kv._2 != actor)
            newRoomInfo = decreaseActualPlayer(roomInfo,newReady.keySet)
            notifyUpdateRoomInfo(newSubscribers, newReady, newRoomInfo)
          } else {
            newSubscribers = subscribersRoom - actor
          }
          if(newSubscribers.isEmpty && newReady.isEmpty){
            lobby ! EmptyRoom(roomInfo.basicInfo.name)
            context.log.info("Everybody into the room logout. Room is empty and behavior stopped")
            Behaviors.stopped
          } else {
            updateBehavior(updatedSub = newSubscribers, updatedReady = newReady, updatedRoomInfo = newRoomInfo)
          }
      }
    }
  }
}
