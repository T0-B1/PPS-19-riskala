package org.riskala.Model.Room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
        //TODO: change new PlayerMessage into info
        newReady.foreach(rp => rp._2 ! new PlayerMessage {})
        newSubscribers. foreach(s => s ! new PlayerMessage {})
        //TODO: newRoomInfo
        lobby ! new LobbyMessage {}
      }

      def updateBehavior(updatedSub: HashSet[ActorRef[PlayerMessage]] = subscribersRoom,
                         updatedReady: HashMap[String,ActorRef[PlayerMessage]] = readyPlayerList,
                         updatedRoomInfo: RoomInfo = roomInfo,
                         updatedLobby: ActorRef[LobbyMessage] = lobby
                        ):Behavior[RoomMessage] = {
        println("updSub: -> "+ updatedSub.toList)
        println("updReady: -> "+ updatedReady.toList)
        println("updRI: -> "+ updatedRoomInfo)
        println("updLobby: -> "+ updatedLobby)
        println( "------- --------")
        roomManager(updatedSub, updatedReady, updatedRoomInfo, updatedLobby)
      }

      message match {
        case Join(actor) =>
          println("beforeJoin "+subscribersRoom.size)
          val newSubscriber = subscribersRoom + actor
          println("After SUB "+newSubscriber.size)
          //TODO: change into info
          actor ! new PlayerMessage {}
          updateBehavior(updatedSub = newSubscriber)

        case Leave(actor) =>
          println("LEAVE")
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          if (newSubscriber.isEmpty && readyPlayerList.isEmpty) {
            println("room empty. Bye")
            /*If there is any player:
             (1) send message to lobby (emptyRoom);
             (2)Behaviors.stopped
             */
            //TODO: EmptyRoom(info.basicInfo.name)
            lobby ! new LobbyMessage {}
            Behaviors.stopped
          }
          println("LEAVE DONE")
          updateBehavior(updatedSub = newSubscriber)

        case UnReady(playerName, actor) =>
          println("UNREADY")
          val newReady = readyPlayerList - playerName
          //Update actualNumberPlayer
          val newRoomInfo = roomInfo.copy(
            roomInfo.basicInfo.copy(
              actualNumberOfPlayer = roomInfo.basicInfo.actualNumberOfPlayer - 1))
          val newSubscriber = subscribersRoom + actor
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
          println("UNREADY DONE")
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Ready(playerName, actor) =>
          println("READY")
          //Update actualNumberPlayer
          val newRoomInfo = roomInfo.copy(
            roomInfo.basicInfo.copy(
              actualNumberOfPlayer = roomInfo.basicInfo.actualNumberOfPlayer + 1))
          println("newRoomInfo - "+ newRoomInfo)
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          //Add the actor into readyPlayerList
          val newReady = readyPlayerList + (playerName -> actor)
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)

          if (newReady.size == newRoomInfo.basicInfo.maxNumberOfPlayer) {
            println("Room complete. Start Game")
            //Game can start
            //TODO: StartGame(roomInfo.basicInfo.name, context.self.asInstanceOf[ActorRef[_]])
            lobby ! new LobbyMessage {}
            //TODO: Change behavior from Room to Game -> GameManager()
          }
          println("READY DONE")
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)


        case Logout(actor) =>
          println("LOGOUT")
          val newSubscriber = subscribersRoom - actor
          updateBehavior(updatedSub = newSubscriber)

      }
    }
  }
}
