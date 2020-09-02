package org.riskala.Model

import org.scalatest.wordspec.AnyWordSpec
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import org.riskala.Model.ModelMessages._
import org.scalatest.BeforeAndAfterAll

class LobbyTest extends AnyWordSpec with BeforeAndAfterAll {
  val testKit: ActorTestKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "Lobby")
  val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeSub")
  val probeCreate: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeCreate")
  val probeCreate2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeCreate2")
  val probeJoin: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin")
  val probeJoin2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin2")
  val probeJoin3: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin3")

  "Subscribe to Lobby" should {
    "give Lobby info" in {
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! Logout(probeSub.ref)
      probeSub.expectNoMessage()
    }
  }

  "Creation of a Room in Lobby" should {
    "create the Room and give Lobby info to subs" in {
      lobby ! Subscribe(probeCreate.ref)
      lobby ! Subscribe(probeCreate2.ref)
      probeCreate.expectMessageType[PlayerMessage]
      probeCreate2.expectMessageType[PlayerMessage]
      lobby ! CreateRoom(probeCreate.ref, RoomInfo(RoomBasicInfo("Europa", 0, 4), ""))
      probeCreate.expectNoMessage()
      probeCreate2.expectMessageType[PlayerMessage]
      lobby ! Logout(probeCreate2.ref)
      probeCreate2.expectNoMessage()
    }
  }

  "Join a Room from Lobby" should {
    "remove from Lobby and update info" in {
      lobby ! Subscribe(probeJoin.ref)
      lobby ! Subscribe(probeJoin2.ref)
      lobby ! Subscribe(probeJoin3.ref)
      probeJoin.expectMessageType[PlayerMessage]
      probeJoin2.expectMessageType[PlayerMessage]
      probeJoin3.expectMessageType[PlayerMessage]
      //probe create room
      lobby ! CreateRoom(probeJoin.ref, RoomInfo(RoomBasicInfo("Usa", 0, 6), ""))
      probeJoin.expectNoMessage()
      probeJoin2.expectMessageType[PlayerMessage]
      probeJoin3.expectMessageType[PlayerMessage]
      //probe2 join
      lobby ! JoinTo(probeJoin2.ref, "Usa")
      probeJoin2.expectNoMessage()
      probeJoin3.expectNoMessage()
      //probe3 try join and receive error response
      lobby ! JoinTo(probeJoin3.ref, "America")
      probeJoin3.expectMessageType[PlayerMessage]
      lobby ! Logout(probeJoin3.ref)
      probeJoin3.expectNoMessage()
    }
  }
}
