package org.riskala.Model

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.Model.ModelMessages._

import scala.collection.immutable.HashMap

object RoomManager {
  var subscribersRoom: Set[ActorRef[_]] = Set.empty
  var readyPlayerList: HashMap[String,ActorRef[_]] = HashMap.empty

  def apply(): Behavior[RoomMessage] = {
    Behaviors.receive { (context, message) =>
      message match {

        case Join(actor) => ???

        case Leave(actor) => ???

        case UnReady(playerName, actor) => ???

        case Ready(playerName, actor) => ???
          
        case Logout(actor) => ???

      }
    }
  }
}
