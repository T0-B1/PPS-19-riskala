package org.riskala.controller.actors.game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.player.PlayerMessages.{GameEndMessage, GameInfoMessage, GameReferent, GameUpdateMessage, PlayerMessage}
import org.riskala.controller.actors.Messages.{GameMessage, LobbyMessage, Logout}
import org.riskala.model.{Player, logic}
import org.riskala.model.logic.{Command, Deploy, Event, EventStore, GameInitialized, GameSnapshot, SnapshotGenerator}
import org.riskala.controller.actors.game.GameMessages._
import org.riskala.controller.actors.lobby.LobbyMessages.EndGame
import org.riskala.controller.actors.lobby.LobbyMessages.Subscribe
import org.riskala.utils.Utils
import org.riskala.view.messages.ToClientMessages.GamePersonalInfo

object GameManager {
  /**
   * Creates the GameManeger behavior
   * @param gameName The name of the new game
   * @param subscribers Set of players who subscribe to the game
   * @param participants Map of player who are actually connected to the game
   * @param players The name of players
   * @param scenarioName The name of the scenario
   * @param lobby ActorRef of the lobby
   * */
  def apply(gameName: String,
            subscribers: Set[ActorRef[PlayerMessage]],
            participants: Map[Player,ActorRef[PlayerMessage]],
            players: Set[Player],
            scenarioName: String,
            lobby: ActorRef[LobbyMessage]): Behavior[GameMessage] =
    Behaviors.setup { context =>
      subscribers.foreach(_ ! GameReferent(context.self))
      participants.values.foreach(_ ! GameReferent(context.self))
      val gameSnapshot: GameSnapshot = GameSnapshot.newGame(players.toSeq, scenarioName)
      val eventStore: EventStore[Event] = EventStore(Seq(GameInitialized(gameSnapshot)))
      gameManager(gameName, subscribers, participants, players, scenarioName, lobby, eventStore, gameSnapshot)
    }

  private def gameManager(gameName: String,
                          subscribers: Set[ActorRef[PlayerMessage]],
                          participants: Map[Player,ActorRef[PlayerMessage]],
                          players: Set[Player],
                          scenarioName: String,
                          lobby: ActorRef[LobbyMessage],
                          eventStore: EventStore[Event],
                          gameSnapshot: GameSnapshot): Behavior[GameMessage] =
    Behaviors.receive { (context,message) => {

      def nextBehavior(updateName: String = gameName,
                       updatedSub: Set[ActorRef[PlayerMessage]] = subscribers,
                       updatedParticipants: Map[Player,ActorRef[PlayerMessage]] = participants,
                       updatedPlayers: Set[Player] = players,
                       updateScenario: String = scenarioName,
                       updateLobby: ActorRef[LobbyMessage] = lobby,
                       eventStore: EventStore[Event] = eventStore,
                       gameSnapshot: GameSnapshot = gameSnapshot
                      ): Behavior[GameMessage] =
        gameManager(updateName, updatedSub, updatedParticipants, updatedPlayers, updateScenario, updateLobby, eventStore, gameSnapshot)

      def evolveEventStore(command: Command) : (EventStore[Event], GameSnapshot) = {
        // Executing the command over the state produces a set of new events (a behavior)
        val behavior = command.execution(gameSnapshot)
        // The event store is updated with the new events
        val newEventStore = eventStore.perform(behavior)
        // A new state is computed by projecting the behavior over the old state
        val newSnapshot = SnapshotGenerator().project(gameSnapshot, behavior)
        (newEventStore, newSnapshot)
      }

      def getPersonalInfo(optPlayer: Option[Player], gameSnapshot: GameSnapshot): GamePersonalInfo = {
        optPlayer.fold(GamePersonalInfo())(p=>GamePersonalInfo(gameSnapshot.objectives(p),gameSnapshot.cards(p).toList))
      }

      def notifyUpdate(gameSnapshot: GameSnapshot): Unit = {
        val msgFromPersonalInfo = (personalInfo:GamePersonalInfo) => GameUpdateMessage(gameSnapshot.nowPlaying.nickname,
          gameSnapshot.deployableTroops,
          gameSnapshot.turn<=players.size,
          gameSnapshot.geopolitics,
          personalInfo)
        val handleWin =
          gameSnapshot.winner
            .fold((_:ActorRef[PlayerMessage])=>{})(p=>(a:ActorRef[PlayerMessage])=>{a ! GameEndMessage(p)})
        subscribers.foreach(sub => {
          sub ! msgFromPersonalInfo(GamePersonalInfo())
          handleWin(sub)
        })
        participants.foreach(part => {
          val playerOpt = players.find(_==part._1)
          part._2 ! msgFromPersonalInfo(getPersonalInfo(playerOpt,gameSnapshot))
          handleWin(part._2)
        })
        gameSnapshot.winner.foreach(_ => lobby ! EndGame(gameName,context.self))
      }

      context.log.info(s"GameManager $gameName: $message")

      message match {
        case JoinGame(actor) =>
          actor ! GameReferent(context.self)
          nextBehavior()

        case Leave(actor) =>
          val newSubs = subscribers-actor
          val newPart = participants.filterNot(kv=>kv._2==actor)
          lobby ! Subscribe(actor)
          nextBehavior(updatedSub = newSubs, updatedParticipants = newPart)

        case ActionAttack(playerName, from, to, troops) =>
          val (newEventStore, newSnapshot) = evolveEventStore(logic.Attack(from, to, troops))
          notifyUpdate(newSnapshot)
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case ActionMove(playerName, from, to, troops) =>
          val (newEventStore, newSnapshot) = evolveEventStore(logic.MoveTroops(from, to, troops))
          notifyUpdate(newSnapshot)
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case ActionDeploy(playerName, from, to, troops) =>
          val (newEventStore, newSnapshot) = evolveEventStore(Deploy(to,troops))
          notifyUpdate(newSnapshot)
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case RedeemBonus(playerName, card) =>
          val player = getPlayerByName(players, playerName).get
          val (newEventStore, newSnapshot) = evolveEventStore(logic.RedeemBonus(player, card))
          notifyUpdate(newSnapshot)
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)

        case GetFullInfo(playerName, actor) =>
          val optAskingPlayer = getPlayerByName(gameSnapshot.players.toSet, playerName)
          val newParticipants = optAskingPlayer.fold(participants)(p=>participants + (p -> actor))
          val newSubs = optAskingPlayer.fold(subscribers+actor)(_ => subscribers)
          val personalInfo = getPersonalInfo(optAskingPlayer,gameSnapshot)
          val gameInfoMessage: GameInfoMessage = GameInfoMessage(gameSnapshot.players.map(p=>p.nickname).toSet,
            gameSnapshot.nowPlaying.nickname,
            gameSnapshot.deployableTroops,
            gameSnapshot.scenario,
            gameSnapshot.turn<=players.size,
            gameSnapshot.geopolitics,
            personalInfo,
            gameSnapshot.winner)
          actor ! gameInfoMessage
          nextBehavior(updatedSub = newSubs,updatedParticipants = newParticipants)

        case EndTurn(playerName) =>
          val player = getPlayerByName(players, playerName).get
          val (newEventStore, newSnapshot) = evolveEventStore(logic.EndTurn(player))
          notifyUpdate(newSnapshot)
          val nextPlayer = newSnapshot.nowPlaying
          if(!participants.contains(nextPlayer))
            Utils.sendUserTurnNotification(nextPlayer.nickname, gameName)
          nextBehavior(eventStore = newEventStore, gameSnapshot = newSnapshot)
        case Logout(actor) =>
          val newSubs = subscribers-actor
          val newPart = participants.filterNot(kv=>kv._2==actor)
          nextBehavior(updatedSub = newSubs, updatedParticipants = newPart)
      }
    }
  }

  private def getPlayerByName(players: Set[Player], playerName: String): Option[Player] = {
    players.collectFirst({case p if p.nickname.equals(playerName) => p})
  }

}
