package org.riskala.model.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.model.ModelMessages.{GameMessage, PlayerMessage}

import scala.collection.immutable.HashSet

object GameManager {
  def apply(): Behavior[GameMessage] = nextBehavior(HashSet.empty)

  private def nextBehavior(subscribers: HashSet[ActorRef[PlayerMessage]]): Behavior[GameMessage] =
    Behaviors.receive { (context,message) => {
      Behaviors.ignore[GameMessage]
    }
  }
}
