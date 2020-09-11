package org.riskala.model

import org.scalatest.wordspec.AnyWordSpec
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.controller.actors.PlayerMessages._
import org.riskala.model.lobby.LobbyManager
import org.riskala.model.lobby.LobbyMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.room.RoomMessages.{RoomBasicInfo, RoomInfo}
import org.scalatest.BeforeAndAfterAll

import scala.collection.immutable.{HashMap, HashSet}

class LobbyTest extends AnyWordSpec with BeforeAndAfterAll {
  val testKit: ActorTestKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val europe: String = "Europe"
  val europeBasicInfo: RoomBasicInfo = RoomBasicInfo(europe, 0, 4)
  val europeInfo: RoomInfo = RoomInfo(europeBasicInfo, "")
  val emptyStringList: List[String] = List.empty[String]
  val emptyRoomNameInfoList: List[RoomNameInfo] = List.empty[RoomNameInfo]

  "Subscribe to Lobby" should {
    "give Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbySub")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeSub")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)))
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
      probeCreate.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)))
      probeCreate2.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)))
      lobby ! CreateRoom(probeCreate.ref, europeInfo)
      probeCreate2.expectMessage(LobbyInfoMessage(LobbyInfo(List(RoomNameInfo(europe,"0/4")),emptyStringList,emptyStringList)))
    }
  }

  "Join a Room from Lobby" should {
    "remove from Lobby and update info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyJoin")
      val probeJoin: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin")
      val probeJoin2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin2")
      val probeJoin3: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin3")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(List(RoomNameInfo("Usa","0/6")),emptyStringList,emptyStringList)
      lobby ! Subscribe(probeJoin.ref)
      lobby ! Subscribe(probeJoin2.ref)
      lobby ! Subscribe(probeJoin3.ref)
      probeJoin.expectMessage(LobbyInfoMessage(emptyLobby))
      probeJoin2.expectMessage(LobbyInfoMessage(emptyLobby))
      probeJoin3.expectMessage(LobbyInfoMessage(emptyLobby))
      //probe create room
      lobby ! CreateRoom(probeJoin.ref, RoomInfo(RoomBasicInfo("Usa", 0, 6), ""))
      probeJoin2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      probeJoin3.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      //probe2 join
      lobby ! JoinTo(probeJoin2.ref, "Usa")
      probeJoin3.expectNoMessage()
      //probe3 try join and receive error response
      lobby ! JoinTo(probeJoin3.ref, "America")
      probeJoin3.expectMessage(ErrorMessage("Room not found"))
    }
  }

  "StartGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyStart")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeStart")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessageType[LobbyInfoMessage]
      lobby ! StartGame(RoomInfo(RoomBasicInfo(europe, 4,4), europe),
        HashMap.empty[String,ActorRef[PlayerMessage]],
        HashSet.empty[ActorRef[PlayerMessage]])
      probeSub.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,List(europe),emptyStringList)))
    }
  }

  "EndGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyEnd")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameEnd")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEnd")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,List(europe))
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! EndGame(europe,game)
      probeSub.expectMessage(LobbyInfoMessage(nonEmptyLobby))
    }
  }

  "CloseGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyClose")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameClose")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeClose")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,List(europe))
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! EndGame(europe,game)
      probeSub.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      lobby ! GameClosed(europe, List.empty[ActorRef[PlayerMessage]])
      probeSub.expectMessage(LobbyInfoMessage(nonEmptyLobby))
    }
  }

  "UpdateRoom to Lobby" should {
    "update the Room info and notify subs" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyUpdate")
      val probeUpdate: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeUpdate")
      val probeUpdate2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeUpdate2")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(List(RoomNameInfo(europe,"0/4")),emptyStringList,emptyStringList)
      lobby ! Subscribe(probeUpdate.ref)
      lobby ! Subscribe(probeUpdate2.ref)
      probeUpdate.expectMessage(LobbyInfoMessage(emptyLobby))
      probeUpdate2.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! CreateRoom(probeUpdate.ref, europeInfo)
      probeUpdate.expectMessage(RoomInfoMessage(europeInfo))
      probeUpdate2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      lobby ! UpdateRoomInfo(europeInfo.basicInfo)
      probeUpdate.expectNoMessage()
      probeUpdate2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
    }
  }

  "EmptyRoom to Lobby" should {
    "remove the Room info and notify subs" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyEmpty")
      val probeEmpty: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEmpty")
      val probeEmpty2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEmpty2")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(List(RoomNameInfo(europe,"0/4")),emptyStringList,emptyStringList)
      lobby ! Subscribe(probeEmpty.ref)
      lobby ! Subscribe(probeEmpty2.ref)
      probeEmpty.expectMessage(LobbyInfoMessage(emptyLobby))
      probeEmpty2.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! CreateRoom(probeEmpty.ref, europeInfo)
      probeEmpty.expectMessage(RoomInfoMessage(europeInfo))
      probeEmpty2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      lobby ! EmptyRoom(europeInfo.basicInfo.name)
      probeEmpty.expectNoMessage()
      probeEmpty2.expectMessage(LobbyInfoMessage(emptyLobby))
    }
  }
}
