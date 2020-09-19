package org.riskala.model.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages.{GameInfoMessage, GameReferent, PlayerMessage}
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, Logout}
import org.riskala.model.{Player, eventsourcing}
import org.riskala.model.eventsourcing.{Command, Deploy, Event, EventStore, GameInitialized, GameSnapshot, SnapshotGenerator}
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
      val gameSnapshot: GameSnapshot = GameSnapshot.newGame(players.toSeq, scenarioName)
      val eventStore: EventStore[Event] = EventStore(Seq(GameInitialized(gameSnapshot)))
      gameManager(gameName, subscribers, players, scenarioName, lobby, eventStore, gameSnapshot)
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

      def evolveEventStore(command: Command) : (EventStore[Event], GameSnapshot) = {
        // Executing the command over the state produces a set of new events (a behavior)
        val behavior = command.execution(gameSnapshot)
        // The event store is updated with the new events
        val newEventStore = eventStore.perform(behavior)
        // A new state is computed by projecting the behavior over the old state
        val newSnapshot = SnapshotGenerator().Project(gameSnapshot, behavior)
        (newEventStore, newSnapshot)
      }

      context.log.info(s"GameManager $gameName: $message")

      message match {
        case JoinGame(actor) =>
          val newSubs = subscribers + actor
          nextBehavior(updatedSub = newSubs)

        case Leave(actor) =>
          lobby ! Subscribe(actor)
          nextBehavior(updatedSub = subscribers - actor)

        case ActionAttack(playerName, from, to, troops) =>
          val (newEventStore, newSnapshot) = evolveEventStore(eventsourcing.Attack(from, to, troops))
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case ActionMove(playerName, from, to, troops) =>
          val (newEventStore, newSnapshot) = evolveEventStore(eventsourcing.MoveTroops(from, to, troops))
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case ActionDeploy(playerName, from, to, troops) =>
          val (newEventStore, newSnapshot) = evolveEventStore(Deploy(to,troops))
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case RedeemBonus(playerName, card) =>
          val player = getPlayerByName(players, playerName).get
          val (newEventStore, newSnapshot) = evolveEventStore(eventsourcing.RedeemBonus(player, card))
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case GetFullInfo(playerName, actor) =>
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
          val player = getPlayerByName(players, playerName).get
          val (newEventStore, newSnapshot) = evolveEventStore(eventsourcing.EndTurn(player))
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case Logout(actor) =>
          nextBehavior(updatedSub = subscribers - actor)
      }
    }
  }

  private def getPlayerByName(players: Set[Player], playerName: String): Option[Player] = {
    players.collectFirst({case p if p.nickname.equals(playerName) => p})
  }

}
