package org.riskala.controller.actors

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.ActorRef
import akka.actor.typed.receptionist.Receptionist
import org.riskala.model.ModelMessages.LobbyMessage
import org.riskala.model.lobby.LobbyManager
import org.scalatest.wordspec.AnyWordSpecLike
import akka.actor.{Actor, Props}
import akka.event.Logging


class PlayerActorTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "Something" must {
    "behave correctly" in {

      // Mocks the lobby lobbyProbe.expectMessage...
      val lobbyProbe = testKit.createTestProbe[LobbyMessage]()
      // Registers the probe, so that the playerActor can find it
      system.receptionist ! Receptionist.register(LobbyManager.lobbyServiceKey, lobbyProbe.ref)

      // Mocks the socket socketProbe.expectMessage
      val socketProbe = testKit.createTestProbe[String]()

      // Spawn an untyped actor that forwards everything to a the typed socketProbe
      val props1 = Props(new UntypedForwarder(socketProbe.ref))
      val untypedForwarder = testKit.system.classicSystem.actorOf(props1)
      
      // Spawn a real playerActor (SUT)
      val playerActor = testKit.spawn(PlayerActor("user", untypedForwarder))
    }
  }
}

class UntypedForwarder(recipient: ActorRef[String]) extends Actor {
  val log = Logging(context.system, this)

  def receive: Receive = {
    case m => {
      log.info(s"Untyped forwarder received: $m from ${sender()}")
      recipient ! m.toString
    }
  }
}
