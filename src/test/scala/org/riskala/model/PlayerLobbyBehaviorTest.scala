package org.riskala.model

import akka.actor.{ActorSystem, typed}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import argonaut.Argonaut._
import org.riskala.controller.actors.PlayerActor
import org.riskala.controller.actors.PlayerMessages.SocketMessage
import org.riskala.model.lobby.LobbyManager
import org.riskala.model.lobby.LobbyMessages.JoinTo
import org.riskala.view.messages.FromClientMessages.JoinMessage
import org.riskala.view.messages.WrappedMessage

class PlayerLobbyBehaviorTest extends TestKit(ActorSystem("PlayerLobbyTest")) with AnyWordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "a Lobby" should{
    "be registered in a RegisterList" in {
      val typedSystem: typed.ActorSystem[Nothing] = system.toTyped
      val lobby = typedSystem.systemActorOf(LobbyManager(), "LobbyActor")
      typedSystem.receptionist ! Receptionist.Register(LobbyManager.lobbyServiceKey, lobby.ref)
    }
  }

  "A player" should{
    "receive a message over the socket" in {
      val socket: TestProbe = TestProbe()
      val typedSystem: typed.ActorSystem[Nothing] = system.toTyped
      val lobby = typedSystem.systemActorOf(LobbyManager(), "LobbyActorSocket")
      typedSystem.receptionist ! Receptionist.Register(LobbyManager.lobbyServiceKey, lobby.ref)
      val playerActor = typedSystem.systemActorOf(PlayerActor("Ale", socket.ref), "playerAle")
      playerActor ! SocketMessage(
        WrappedMessage("JoinMessage", JoinMessage("ROOM1").asJson.pretty(nospace)).asJson.pretty(nospace))
      lobby ! JoinTo(playerActor.ref, "ROOM1")
      expectNoMessage()
    }
  }
}