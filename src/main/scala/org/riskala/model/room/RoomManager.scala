package org.riskala.model.room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.game.GameManager
import org.riskala.model.lobby.LobbyMessages.{EmptyRoom, StartGame, UpdateRoomInfo}

import scala.collection.immutable.{HashMap, HashSet}

object RoomManager {

  def apply(roomInfo: RoomInfo, lobby: ActorRef[LobbyMessage]): Behavior[RoomMessage] =
    roomManager(HashSet.empty, HashMap.empty, roomInfo, lobby)

  def roomManager(subscribersRoom: HashSet[ActorRef[PlayerMessage]],
                  readyPlayerList: HashMap[String,ActorRef[PlayerMessage]],
                  roomInfo: RoomInfo,
                  lobby: ActorRef[LobbyMessage]
                 ):Behavior[RoomMessage] = {

    Behaviors.receive { (context, message) =>

      def notifyUpdateRoomInfo(newSubscribers: HashSet[ActorRef[PlayerMessage]],
                               newReady: HashMap[String,ActorRef[PlayerMessage]],
                               newRoomInfo: RoomInfo): Unit = {
        
        newReady.foreach(rp => rp._2 ! RoomInfoMessage(newRoomInfo))
        newSubscribers. foreach(s => s ! RoomInfoMessage(newRoomInfo))

        lobby ! UpdateRoomInfo(newRoomInfo.basicInfo)
      }

      def updateBehavior(updatedSub: HashSet[ActorRef[PlayerMessage]] = subscribersRoom,
                         updatedReady: HashMap[String,ActorRef[PlayerMessage]] = readyPlayerList,
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
          context.log.info("beforeJoin "+subscribersRoom.size)
          val newSubscriber = subscribersRoom + actor
          context.log.info("After SUB "+newSubscriber.size)

          actor ! RoomInfoMessage(roomInfo)
          updateBehavior(updatedSub = newSubscriber)

        case Leave(actor) =>
          context.log.info("LEAVE")
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          if (newSubscriber.isEmpty && readyPlayerList.isEmpty) {
            context.log.info("room empty. Bye")
            /*If there is any player:
             (1) send message to lobby (emptyRoom);
             (2)Behaviors.stopped
             */

            lobby ! EmptyRoom(roomInfo.basicInfo.name)
            Behaviors.stopped
          }
          context.log.info("LEAVE DONE")
          updateBehavior(updatedSub = newSubscriber)

        case UnReady(playerName, actor) =>
          context.log.info("UNREADY")
          val newReady = readyPlayerList - playerName
          //Update actualNumberPlayer
          val newRoomInfo = roomInfo.copy(
            roomInfo.basicInfo.copy(
              actualNumberOfPlayer = roomInfo.basicInfo.actualNumberOfPlayer - 1))
          val newSubscriber = subscribersRoom + actor
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
          context.log.info("UNREADY DONE")
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Ready(playerName, actor) =>
          context.log.info("READY")
          //Update actualNumberPlayer
          val newRoomInfo = roomInfo.copy(
            roomInfo.basicInfo.copy(
              actualNumberOfPlayer = roomInfo.basicInfo.actualNumberOfPlayer + 1))
          context.log.info("newRoomInfo - "+ newRoomInfo)
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          //Add the actor into readyPlayerList
          val newReady = readyPlayerList + (playerName -> actor)
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)

          if (newReady.size == newRoomInfo.basicInfo.maxNumberOfPlayer) {
            context.log.info("Room complete. Start Game")
            //Game can start

            lobby ! StartGame(roomInfo, context.self.asInstanceOf[ActorRef[GameMessage]])
            //TODO: Change behavior from Room to Game -> GameManager()

            context.spawn(GameManager(), "GameManager")
          }
          context.log.info("READY DONE")
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Logout(actor) =>
          context.log.info("LOGOUT")
          val newSubscriber = subscribersRoom - actor
          updateBehavior(updatedSub = newSubscriber)

      }
    }
  }
}
