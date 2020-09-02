package org.riskala.Model.Room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages.{LobbyMessage, _}

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
                               newReady: HashMap[String,ActorRef[PlayerMessage]]): Unit = {
        //TODO: change new PlayerMessage into info
        newReady.foreach(rp => rp._2 ! new PlayerMessage {})
        newSubscribers. foreach(s => s ! new PlayerMessage {})
        //TODO: UpdateRoomInfo(info.basicInfo)
        lobby ! new LobbyMessage {}
      }

      def updateBehavior(updatedSub: HashSet[ActorRef[PlayerMessage]] = subscribersRoom,
                         updatedReady: HashMap[String,ActorRef[PlayerMessage]] = readyPlayerList,
                         updatedRoomInfo: RoomInfo = roomInfo,
                         updatedLobby: ActorRef[LobbyMessage] = lobby
                        ):Behavior[RoomMessage] = roomManager(updatedSub, updatedReady, updatedRoomInfo, updatedLobby)

      message match {

        case Join(actor) =>
          val newSubscriber = subscribersRoom + actor
          //TODO: change into info
          actor ! new PlayerMessage {}
          updateBehavior(updatedSub = newSubscriber)

        case Leave(actor) =>
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          if (newSubscriber.isEmpty && readyPlayerList.isEmpty) {
            /*If there is any player:
             (1) send message to lobby (emptyRoom);
             (2)Behaviors.stopped
             */
            //TODO: EmptyRoom(info.basicInfo.name)
            lobby ! new LobbyMessage {}
            Behaviors.stopped
          }
          updateBehavior(updatedSub = newSubscriber)

        case UnReady(playerName, actor) =>
          val newReady = readyPlayerList - playerName
          //Update actualNumberPlayer
          roomInfo.basicInfo.actualNumberOfPlayer -= 1
          val newSubscriber = subscribersRoom + actor
          notifyUpdateRoomInfo(newSubscriber, newReady)
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady)

        case Ready(playerName, actor) =>
          //Update actualNumberPlayer
          roomInfo.basicInfo.actualNumberOfPlayer += 1
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          //Add the actor into readyPlayerList
          val newReady = readyPlayerList + (playerName -> actor)
          notifyUpdateRoomInfo(newSubscriber, newReady)

          if (roomInfo.basicInfo.actualNumberOfPlayer == roomInfo.basicInfo.maxNumberOfPlayer) {
            //Game can start
            //TODO: StartGame(roomInfo.basicInfo.name, context.self.asInstanceOf[ActorRef[_]])
            lobby ! new LobbyMessage {}
            //TODO: Change behavior from Room to Game -> GameManager()
          }
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady)

        case Logout(actor) =>
          val newSubscriber = subscribersRoom - actor
          updateBehavior(updatedSub = newSubscriber)

      }
    }
  }
}
