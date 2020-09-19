package org.riskala.model.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.{GameInfoMessage, GameReferent, PlayerMessage}
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, Logout}
import org.riskala.model.Player
import org.riskala.model.eventsourcing.GameSnapshot
import org.riskala.model.game.GameMessages._
import org.riskala.model.lobby.LobbyMessages.Subscribe
import org.riskala.utils.MapLoader
import org.riskala.view.messages.ToClientMessages.{GameFullInfo, GamePersonalInfo, RoomInfo}

import scala.collection.immutable.{HashMap, HashSet}

object GameManager {
  def apply(gameName: String,
            subscribers: Set[ActorRef[PlayerMessage]],
            players: Set[Player],
            scenarioName: String,
            lobby: ActorRef[LobbyMessage]): Behavior[GameMessage] =
    Behaviors.setup { context =>
      subscribers.foreach(_ ! GameReferent(context.self))
      //TODO: event sourcing, scenario and GameFullInfo
      gameManager(gameName, subscribers, players, scenarioName, lobby)
    }

  private def gameManager(gameName: String,
                          subscribers: Set[ActorRef[PlayerMessage]],
                          players: Set[Player],
                          scenarioName: String,
                          lobby: ActorRef[LobbyMessage]): Behavior[GameMessage] =
    Behaviors.receive { (context,message) => {

      def nextBehavior(updateName: String = gameName,
                       updatedSub: Set[ActorRef[PlayerMessage]] = subscribers,
                       updatedPlayers: Set[Player] = players,
                       updateScenario: String = scenarioName,
                       updateLobby: ActorRef[LobbyMessage] = lobby
                      ): Behavior[GameMessage] =
        gameManager(updateName, updatedSub, updatedPlayers, updateScenario, updateLobby)

      message match {
        case JoinGame(actor) =>
          context.log.info("Join")
          val newSubs = subscribers + actor
          nextBehavior(updatedSub = newSubs)

        case Leave(actor) =>
          context.log.info("Leave")
          lobby ! Subscribe(actor)
          nextBehavior(updatedSub = subscribers - actor)

        case Action(playerName, from, to, troops) =>
          context.log.info("Action")
          nextBehavior()

        case RedeemBonus(playerName, card) =>
          context.log.info("RedeemBonus")
          nextBehavior()

        case GetFullInfo(playerName, actor) =>
          context.log.info("GetFullInfo")
          val player = Player(playerName,"")
          val starterGame = GameSnapshot.newGame(players.toSeq,MapLoader.loadMap("italy").get)
          val personalInfo =
            if(players.contains(player)) GamePersonalInfo(starterGame.objectives(player),starterGame.cards(player).toList)
            else GamePersonalInfo()
          val gameInfo = GameInfoMessage(starterGame.players.map(_.nickname).toSet,
            starterGame.nowPlaying.nickname,
            starterGame.deployableTroops,
            starterGame.scenario,
            starterGame.geopolitics,
            personalInfo)
          actor ! gameInfo
          nextBehavior()

        case EndTurn(playerName) =>
          context.log.info("EndTurn")
          nextBehavior()

        case Logout(actor) =>
          context.log.info("Logout")
          nextBehavior(updatedSub = subscribers - actor)
      }
    }
  }
}
