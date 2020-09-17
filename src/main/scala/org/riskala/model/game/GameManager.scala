package org.riskala.model.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.PlayerMessage
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage}
import org.riskala.model.game.GameMessages._
import org.riskala.model.lobby.LobbyMessages.Subscribe
import org.riskala.view.messages.ToClientMessages.RoomInfo

import scala.collection.immutable.{HashMap, HashSet}

object GameManager {
  def apply(gameName: String,
            subscribers: Set[ActorRef[PlayerMessage]],
            players: Set[String],
            scenarioName: String,
            lobby: ActorRef[LobbyMessage]): Behavior[GameMessage] =
    Behaviors.setup { context =>
      //TODO: event sourcing, scenario
      gameManager("", Set.empty, Map.empty, "", lobby)
    }


  private def gameManager(gameName: String,
                          subscribers: Set[ActorRef[PlayerMessage]],
                          players: Map[ActorRef[PlayerMessage], String],
                          scenarioName: String,
                          lobby: ActorRef[LobbyMessage]): Behavior[GameMessage] =
    Behaviors.receive { (context,message) => {

      def nextBehavior(updateName: String = gameName,
                       updatedSub: Set[ActorRef[PlayerMessage]] = subscribers,
                       updatedPlayers: Map[ActorRef[PlayerMessage], String] = players,
                       updateScenario: String = scenarioName,
                       updateLobby: ActorRef[LobbyMessage] = lobby
                      ): Behavior[GameMessage] =
        gameManager(updateName, updatedSub, updatedPlayers, updateScenario, updateLobby)

      message match {
        case JoinGame(actor) =>
          context.log.info("Join")
          val newSubs = subscribers + actor
          //actor ! GameFullInfo()
          nextBehavior(updatedSub = newSubs)

        case Leave(actor) =>
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
