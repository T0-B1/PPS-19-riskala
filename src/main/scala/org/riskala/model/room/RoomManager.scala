package org.riskala.model.room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.lobby.LobbyMessages.{EmptyRoom, StartGame}

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
        context.log.debug("updSub: -> "+ updatedSub.toList)
        context.log.debug("updReady: -> "+ updatedReady.toList)
        context.log.debug("updRI: -> "+ updatedRoomInfo)
        context.log.debug("updLobby: -> "+ updatedLobby)
        context.log.debug( "------- --------")
        roomManager(updatedSub, updatedReady, updatedRoomInfo, updatedLobby)
      }

      message match {
        case Join(actor) =>
          context.log.debug("beforeJoin "+subscribersRoom.size)
          val newSubscriber = subscribersRoom + actor
          context.log.debug("After SUB "+newSubscriber.size)
          //TODO: change into info
          actor ! new PlayerMessage {}
          updateBehavior(updatedSub = newSubscriber)

        case Leave(actor) =>
          context.log.debug("LEAVE")
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          if (newSubscriber.isEmpty && readyPlayerList.isEmpty) {
            context.log.debug("room empty. Bye")
            /*If there is any player:
             (1) send message to lobby (emptyRoom);
             (2)Behaviors.stopped
             */
            //TODO: call Empty room
            lobby ! EmptyRoom(roomInfo.basicInfo.name)
            Behaviors.stopped
          }
          context.log.debug("LEAVE DONE")
          updateBehavior(updatedSub = newSubscriber)

        case UnReady(playerName, actor) =>
          context.log.debug("UNREADY")
          val newReady = readyPlayerList - playerName
          //Update actualNumberPlayer
          val newRoomInfo = roomInfo.copy(
            roomInfo.basicInfo.copy(
              actualNumberOfPlayer = roomInfo.basicInfo.actualNumberOfPlayer - 1))
          val newSubscriber = subscribersRoom + actor
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)
          context.log.debug("UNREADY DONE")
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Ready(playerName, actor) =>
          context.log.debug("READY")
          //Update actualNumberPlayer
          val newRoomInfo = roomInfo.copy(
            roomInfo.basicInfo.copy(
              actualNumberOfPlayer = roomInfo.basicInfo.actualNumberOfPlayer + 1))
          context.log.debug("newRoomInfo - "+ newRoomInfo)
          //Remove the actor from subscribersList
          val newSubscriber = subscribersRoom - actor
          //Add the actor into readyPlayerList
          val newReady = readyPlayerList + (playerName -> actor)
          notifyUpdateRoomInfo(newSubscriber, newReady, newRoomInfo)

          if (newReady.size == newRoomInfo.basicInfo.maxNumberOfPlayer) {
            context.log.debug("Room complete. Start Game")
            //Game can start
            //TODO: StartGame()
            lobby ! StartGame(roomInfo.basicInfo.name, context.self.asInstanceOf[ActorRef[GameMessage]])
            //TODO: Change behavior from Room to Game -> GameManager()

            //return Behaviors.ignore[GameMessage]
          }
          context.log.debug("READY DONE")
          updateBehavior(updatedSub = newSubscriber, updatedReady = newReady, updatedRoomInfo = newRoomInfo)

        case Logout(actor) =>
          context.log.debug("LOGOUT")
          val newSubscriber = subscribersRoom - actor
          updateBehavior(updatedSub = newSubscriber)

      }
    }
  }
}
