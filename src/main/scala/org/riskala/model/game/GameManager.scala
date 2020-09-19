package org.riskala.model.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.{GameInfoMessage, GameReferent, PlayerMessage}
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, Logout}
import org.riskala.model.{Player, eventsourcing}
import org.riskala.model.eventsourcing.{Event, EventStore, GameSnapshot, SnapshotGenerator}
import org.riskala.model.game.GameMessages._
import org.riskala.model.lobby.LobbyMessages.Subscribe
import org.riskala.utils.MapLoader
import org.riskala.view.messages.ToClientMessages.{GameFullInfo, GamePersonalInfo, RoomInfo}
import org.riskala.model._

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
      gameManager(gameName, subscribers, players, scenarioName, lobby, EventStore[Event], GameSnapshot.newGame(players.toSeq, scenarioName)
    }

  private def gameManager(gameName: String,
                          subscribers: Set[ActorRef[PlayerMessage]],
                          players: Set[Player],
                          scenarioName: String,
                          lobby: ActorRef[LobbyMessage],
                          eventStore: EventStore[Event],
                          gameSnapshot: GameSnapshot): Behavior[GameMessage] =
    Behaviors.receive { (context,message) => {

      def nextBehavior(updateName: String = gameName,
                       updatedSub: Set[ActorRef[PlayerMessage]] = subscribers,
                       updatedPlayers: Set[Player] = players,
                       updateScenario: String = scenarioName,
                       updateLobby: ActorRef[LobbyMessage] = lobby,
                       eventStore: EventStore[Event] = eventStore,
                       gameSnapshot: GameSnapshot = gameSnapshot
                      ): Behavior[GameMessage] =
        gameManager(updateName, updatedSub, updatedPlayers, updateScenario, updateLobby, eventStore, gameSnapshot)

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
          val player: Player = players.collectFirst({case p if p.nickname.equals(player) => p}).get
          // Creating a command
          val command = eventsourcing.EndTurn(player)
          // Executing the command over the state produces a set of new events (a behavior)
          val behavior = command.execution(gameSnapshot)
          // The event store is updated with the new events
          val newEventStore = eventStore.perform(behavior)
          // A new state is computed by projecting the behavior over the old state
          val newSnapshot = SnapshotGenerator().Project(gameSnapshot, behavior)
          // New store and snapshot are updated
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case Logout(actor) =>
          context.log.info("Logout")
          nextBehavior(updatedSub = subscribers - actor)
      }
    }
  }
}
