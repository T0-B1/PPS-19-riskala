package org.riskala.Model.Room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._

import scala.collection.immutable.{HashMap, HashSet}

object RoomManager {

  //TODO: to remove
  case class RoomBasicInfo(name: String,
                           actualNumberOfPlayer: Int,
                           maxNumberOfPlayer: Int)

  //TODO: to remove
  case class RoomInfo(basicInfo: RoomBasicInfo, scenario: String)

  def apply(roomInfo: RoomInfo): Behavior[RoomMessage] = {

    roomManager(HashSet.empty, HashMap.empty, roomInfo)
  }

  def roomManager(subscribersRoom: HashSet[ActorRef[PlayerMessage]],
                  readyPlayerList: HashMap[String,ActorRef[PlayerMessage]],
                  roomInfo: RoomInfo
                 ):Behavior[RoomMessage] = {

    Behaviors.receive { (context, message) =>
      message match {

        case Join(actor) =>
          val newSubscriber = subscribersRoom + actor
          //TODO: change into info
          actor ! new PlayerMessage {}
          roomManager(newSubscriber, readyPlayerList, roomInfo)

        case Leave(actor) => ???

        case UnReady(playerName, actor) => ???

        case Ready(playerName, actor) => ???

        case Logout(actor) =>
          val newSubscriber = subscribersRoom - actor
          Behaviors.same

      }
    }
  }
}
