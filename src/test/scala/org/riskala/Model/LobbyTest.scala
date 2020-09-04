package org.riskala.model

import org.scalatest.wordspec.AnyWordSpec
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.model.lobby.LobbyManager
import org.riskala.model.lobby.LobbyMessages._
import org.riskala.model.ModelMessages._
import org.scalatest.BeforeAndAfterAll

class LobbyTest extends AnyWordSpec with BeforeAndAfterAll {
  val testKit: ActorTestKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "Subscribe to Lobby" should {
    "give Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbySub")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeSub")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! Logout(probeSub.ref)
      probeSub.expectNoMessage()
    }
  }

  "Creation of a Room in Lobby" should {
    "create the Room and give Lobby info to subs" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyCreate")
      val probeCreate: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeCreate")
      val probeCreate2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeCreate2")
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
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyJoin")
      val probeJoin: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin")
      val probeJoin2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin2")
      val probeJoin3: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin3")
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

  "StartGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyStart")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameStart")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeStart")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! StartGame("Europa",game)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! Logout(probeSub.ref)
      probeSub.expectNoMessage()
    }
  }

  "EndGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyEnd")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameEnd")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEnd")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! EndGame("Europa",game)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! Logout(probeSub.ref)
      probeSub.expectNoMessage()
    }
  }

  "CloseGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyClose")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameClose")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeClose")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! EndGame("Europa",game)
      probeSub.expectMessageType[PlayerMessage]
      lobby ! GameClosed("Europa", List.empty[ActorRef[PlayerMessage]])
      probeSub.expectMessageType[PlayerMessage]
      lobby ! Logout(probeSub.ref)
      probeSub.expectNoMessage()
    }
  }

  "UpdateRoom to Lobby" should {
    "update the Room info and notify subs" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyUpdate")
      val probeUpdate: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeUpdate")
      val probeUpdate2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeUpdate2")
      val info = RoomInfo(RoomBasicInfo("Europa", 0, 4), "")
      lobby ! Subscribe(probeUpdate.ref)
      lobby ! Subscribe(probeUpdate2.ref)
      probeUpdate.expectMessageType[PlayerMessage]
      probeUpdate2.expectMessageType[PlayerMessage]
      lobby ! CreateRoom(probeUpdate.ref, info)
      probeUpdate.expectNoMessage()
      probeUpdate2.expectMessageType[PlayerMessage]
      lobby ! UpdateRoomInfo(info.basicInfo)
      probeUpdate.expectNoMessage()
      probeUpdate2.expectMessageType[PlayerMessage]
      lobby ! Logout(probeUpdate2.ref)
      probeUpdate2.expectNoMessage()
    }
  }

  "EmptyRoom to Lobby" should {
    "remove the Room info and notify subs" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyEmpty")
      val probeEmpty: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEmpty")
      val probeEmpty2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEmpty2")
      val info = RoomInfo(RoomBasicInfo("Europa", 0, 4), "")
      lobby ! Subscribe(probeEmpty.ref)
      lobby ! Subscribe(probeEmpty2.ref)
      probeEmpty.expectMessageType[PlayerMessage]
      probeEmpty2.expectMessageType[PlayerMessage]
      lobby ! CreateRoom(probeEmpty.ref, info)
      probeEmpty.expectNoMessage()
      probeEmpty2.expectMessageType[PlayerMessage]
      lobby ! EmptyRoom(info.basicInfo.name)
      probeEmpty.expectNoMessage()
      probeEmpty2.expectMessageType[PlayerMessage]
      lobby ! Logout(probeEmpty2.ref)
      probeEmpty2.expectNoMessage()
    }
  }
}
