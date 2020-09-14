package org.riskala.model.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.model.ModelMessages.GameMessage
import org.riskala.model.game.GameMessages._
import org.riskala.view.messages.ToClientMessages.RoomInfo

import scala.collection.immutable.{HashMap, HashSet}

object GameManager {
  def apply(roomInfo: RoomInfo, subscribers: HashSet[ActorRef[PlayerMessage]],
            players: HashMap[String,ActorRef[PlayerMessage]]): Behavior[GameMessage] =
    gameManager(roomInfo, HashSet.empty, HashMap.empty)

  private def gameManager(newRoomInfo: RoomInfo,
                           newSubscribers: HashSet[ActorRef[PlayerMessage]],
                           newPlayers: HashMap[String,ActorRef[PlayerMessage]]): Behavior[GameMessage] =
    Behaviors.receive { (context,message) => {

      def nextBehavior(nextRoomInfo: RoomInfo = newRoomInfo,
                       nextSubscribers: HashSet[ActorRef[PlayerMessage]] = newSubscribers,
                       nextPlayers: HashMap[String,ActorRef[PlayerMessage]] = newPlayers
                      ): Behavior[GameMessage] = gameManager(nextRoomInfo, nextSubscribers,nextPlayers)
      message match {
        case JoinGame(actor) =>
          context.log.info("Join")
          nextBehavior()

        case Leave() =>
          context.log.info("Leave")
          nextBehavior()

        case Deploy() =>
          context.log.info("Deploy")
          nextBehavior()

        case Attack() =>
          context.log.info("Attack")
          nextBehavior()

        case Move() =>
          context.log.info("Move")
          nextBehavior()

        case RedeemBonus() =>
          context.log.info("RedeemBonus")
          nextBehavior()

        case EndTurn() =>
          context.log.info("EndTurn")
          nextBehavior()
      }
    }
  }
}
