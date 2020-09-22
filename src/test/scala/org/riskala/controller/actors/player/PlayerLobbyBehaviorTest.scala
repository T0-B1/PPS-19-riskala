package org.riskala.controller.actors.player

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import akka.actor.{ActorSystem, typed}
import akka.testkit.{TestKit, TestProbe}
import argonaut.Argonaut._
import org.junit.runner.RunWith
import org.riskala.client.messages.FromClientMessages.JoinMessage
import org.riskala.client.messages.WrappedMessage
import org.riskala.controller.actors.lobby.LobbyManager
import org.riskala.controller.actors.lobby.LobbyMessages.JoinTo
import org.riskala.controller.actors.player.PlayerMessages.SocketMessage
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
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