package org.riskala.Model.Room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages.{LobbyMessage, _}

import scala.collection.immutable.{HashMap, HashSet}

object RoomManager {

  //TODO: to remove
  case class RoomBasicInfo(name: String,
                           actualNumberOfPlayer: Int,
                           maxNumberOfPlayer: Int)

  //TODO: to remove
  case class RoomInfo(basicInfo: RoomBasicInfo, scenario: String)

  def apply(roomInfo: RoomInfo, lobby: ActorRef[LobbyMessage]): Behavior[RoomMessage] = {

    roomManager(HashSet.empty, HashMap.empty, roomInfo, lobby)
  }

  def notifyUpdateRoomInfo(newReady: HashMap[String,ActorRef[PlayerMessage]],
                           newSubscribers: HashSet[ActorRef[PlayerMessage]],
                          lobby: ActorRef[LobbyMessage]): Unit = {
    //TODO: change new PlayerMessage into info
    newReady.foreach(rp => rp._2 ! new PlayerMessage {})
    newSubscribers. foreach(s => s ! new PlayerMessage {})
    //TODO: UpdateRoomInfo(info.basicInfo)
    lobby ! new LobbyMessage {}
  }

  def roomManager(subscribersRoom: HashSet[ActorRef[PlayerMessage]],
                  readyPlayerList: HashMap[String,ActorRef[PlayerMessage]],
                  roomInfo: RoomInfo,
                  lobby: ActorRef[LobbyMessage]
                 ):Behavior[RoomMessage] = {

    Behaviors.receive { (context, message) =>
      message match {

        case Join(actor) =>
          val newSubscriber = subscribersRoom + actor
          //TODO: change into info
          actor ! new PlayerMessage {}
          roomManager(newSubscriber, readyPlayerList, roomInfo, lobby)

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
          roomManager(newSubscriber, readyPlayerList, roomInfo, lobby)

        case UnReady(playerName, actor) =>
          val newReady = readyPlayerList - playerName
          //Update actualNumberPlayer
          roomInfo.basicInfo.actualNumberOfPlayer -= 1
          val newSubscribers = subscribersRoom + actor
          notifyUpdateRoomInfo(newReady, newSubscribers, lobby)
          roomManager(newSubscribers, newReady, roomInfo,lobby)

        case Ready(playerName, actor) => ???

        case Logout(actor) =>
          val newSubscriber = subscribersRoom - actor
          Behaviors.same

      }
    }
  }
}
