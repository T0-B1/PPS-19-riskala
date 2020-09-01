package org.riskala.Model

import org.scalatest.wordspec.AnyWordSpec
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import org.riskala.Model.ModelMessages._
import org.scalatest.BeforeAndAfterAll

class LobbyTest extends AnyWordSpec with BeforeAndAfterAll {
  val testKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val lobby = testKit.spawn(LobbyManager(), "Lobby")
  val probeCreate = testKit.createTestProbe[PlayerMessage]("probeCreate")
  val probe2Create = testKit.createTestProbe[PlayerMessage]("probe2Create")
  val probeJoin = testKit.createTestProbe[PlayerMessage]("probeJoin")
  val probe2Join = testKit.createTestProbe[PlayerMessage]("probe2Join")
  val probe3Join = testKit.createTestProbe[PlayerMessage]("probe3Join")

  "Lobby" should {
    "accept subscriber" in {
      val probe = testKit.createTestProbe[PlayerMessage]("probeSub")
      lobby ! Subscribe(probe.ref)
      probe.expectMessageType[PlayerMessage]
      lobby ! Logout(probe.ref)
    }
  }

  "Lobby" should {
    lobby ! Subscribe(probeCreate.ref)
    lobby ! Subscribe(probe2Create.ref)
    probeCreate.expectMessageType[PlayerMessage]
    probe2Create.expectMessageType[PlayerMessage]
    "accept creator" in {
      lobby ! CreateRoom(probeCreate.ref, RoomInfo(RoomBasicInfo("Europa", 0, 4), ""))
      probeCreate.expectNoMessage()
      probe2Create.expectMessageType[PlayerMessage]
      lobby ! Logout(probe2Create.ref)
    }
  }

  "Lobby" should {
    lobby ! Subscribe(probeJoin.ref)
    lobby ! Subscribe(probe2Join.ref)
    lobby ! Subscribe(probe3Join.ref)
    probeJoin.expectMessageType[PlayerMessage]
    probe2Join.expectMessageType[PlayerMessage]
    probe3Join.expectMessageType[PlayerMessage]

    lobby ! CreateRoom(probeJoin.ref, RoomInfo(RoomBasicInfo("Usa", 0, 6), ""))
    probeJoin.expectNoMessage()
    probe2Join.expectMessageType[PlayerMessage]
    probe3Join.expectMessageType[PlayerMessage]
    "accept join to a room" in  {
      lobby ! JoinTo(probe2Join.ref, "Usa")
      probe2Join.expectNoMessage()
    }
  }
/*
  "Lobby" should {

    lobby ! Subscribe(probeJoin.ref)
    probeJoin.expectMessageType[PlayerMessage]
    lobby ! Subscribe(probe2Join.ref)
    probe2Join.expectMessageType[PlayerMessage]

    lobby ! Subscribe(probe3Join.ref)
    probe3Join.expectMessageType[PlayerMessage]
    lobby ! CreateRoom(probeJoin.ref, RoomInfo(RoomBasicInfo("Europa2", 0, 4), ""))
    probeJoin.expectNoMessage()
    probe2Join.expectMessageType[PlayerMessage]
    probe3Join.expectMessageType[PlayerMessage]
    "accept join to a room" in {
      lobby ! JoinTo(probe2Join.ref, "Europa2")
      probeJoin.expectNoMessage()
    }
    "not accept join to a room" in {
      lobby ! JoinTo(probe3Join.ref, "USA")
      probe3Join.expectMessageType[PlayerMessage]
      lobby ! Logout(probe3Join.ref)
    }
  }
*/
  /*"Lobby" should {
    val probe = testKit.createTestProbe[PlayerMessage]()
    lobby ! Subscribe(probe.ref)
    probe.expectMessageType[PlayerMessage]
    lobby ! CreateRoom(probe.ref, RoomInfo(RoomBasicInfo("Europa2", 0, 4), ""))
    probe.expectMessageType[PlayerMessage]
    lobby ! JoinTo(probe.ref, "Europa2")
    "start game" in {
      lobby ! StartGame()
    }
  }*/



}
