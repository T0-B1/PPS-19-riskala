package org.riskala.Model.Room

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import RoomMessages._

import scala.collection.immutable.{HashMap, HashSet}

object RoomManager {

  var subscribersRoom: HashSet[ActorRef[PlayerMessage]] = HashSet.empty
  var readyPlayerList: HashMap[String,ActorRef[PlayerMessage]] = HashMap.empty

  def apply(): Behavior[RoomMessage] = {
    Behaviors.receive { (context, message) =>
      message match {

        case Join(actor) => ???


        case Leave(actor) => ???

        case UnReady(playerName, actor) => ???

        case Ready(playerName, actor) => ???

        case Logout(actor) =>
          subscribersRoom = subscribersRoom - actor
          Behaviors.same

      }
    }
  }
}
