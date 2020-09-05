package org.riskala.controller.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import org.riskala.controller.actors.ServerMessages._

object PlayerActor {

  def apply(username: String): Behavior[PlayerMessage] = {
    playerActor(username)
  }

  private def playerActor(username: String): Behavior[PlayerMessage] =
    Behaviors.setup{ ctx =>
      println(s"PlayerActor of $username started")
      Behaviors.receive { (context, message) =>
        message match {
          case SocketMessage(payload) => {
            println(s"PlayerActor of $username received socket payload: $payload")
            //socket ! TextMessage(s"PlayerActor of $username echoing: $payload")
          }
        }
        Behaviors.same
      }
    }

}