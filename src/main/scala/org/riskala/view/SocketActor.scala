package org.riskala.view

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
object SocketActor {

  trait SocketMessage {
    val payload: String
  }

  case class InBoundSocketMessage(payload: String) extends SocketMessage
  case class OutBoundSocketMessage(payload: String) extends SocketMessage

  def apply(user: String): Behavior[SocketMessage] = Behaviors.receive { (context, message) =>
    message match {
      case InBoundSocketMessage(payload) => {
        println(s"Received $payload from user $user")
        Behaviors.same
      }
    }
  }

}
