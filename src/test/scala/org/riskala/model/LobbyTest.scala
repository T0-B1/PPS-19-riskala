package org.riskala.model

import org.scalatest.wordspec.AnyWordSpec
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import org.junit.runner.RunWith
import org.riskala.controller.actors.player.PlayerMessages._
import org.riskala.model.lobby.LobbyManager
import org.riskala.model.lobby.LobbyMessages._
import org.riskala.model.ModelMessages._
import org.riskala.view.messages.ToClientMessages.{LobbyInfo, RoomBasicInfo, RoomInfo, RoomNameInfo}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.junit.JUnitRunner

import scala.collection.immutable.{HashMap, HashSet}

@RunWith(classOf[JUnitRunner])
class LobbyTest extends AnyWordSpec with BeforeAndAfterAll {
  val testKit: ActorTestKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val mapName: String = "italy"
  val roomName: String = "RoomName"
  val mapBasicInfo: RoomBasicInfo = RoomBasicInfo(roomName, 0, 4)
  val mapInfo: RoomInfo = RoomInfo(mapBasicInfo, Set.empty, "")
  val emptyStringList: Set[String] = Set.empty[String]
  val emptyRoomNameInfoList: Set[RoomNameInfo] = Set.empty[RoomNameInfo]

  "Subscribe to Lobby" should {
    "give Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbySub")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeSub")
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyReferent(lobby.ref))
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
      probeCreate.expectMessage(LobbyReferent(lobby.ref))
      lobby ! Subscribe(probeCreate2.ref)
      probeCreate2.expectMessage(LobbyReferent(lobby.ref))
      probeCreate.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)))
      probeCreate2.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)))
      lobby ! CreateRoom(probeCreate.ref, mapInfo)
      probeCreate2.expectMessage(LobbyInfoMessage(LobbyInfo(Set(RoomNameInfo(roomName,"0/4")),emptyStringList,emptyStringList)))
    }
  }

  "Join a Room from Lobby" should {
    "remove from Lobby and update info" in {
      val roomUsa: String = "UsaRoom"
      val roomAmerica: String = "AmericaRoom"
      val mapUsa: String = "usa"
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyJoin")
      val probeJoin: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin")
      val probeJoin2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin2")
      val probeJoin3: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeJoin3")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(Set(RoomNameInfo(roomUsa,"0/6")),emptyStringList,emptyStringList)
      lobby ! Subscribe(probeJoin.ref)
      probeJoin.expectMessage(LobbyReferent(lobby.ref))
      lobby ! Subscribe(probeJoin2.ref)
      probeJoin2.expectMessage(LobbyReferent(lobby.ref))
      lobby ! Subscribe(probeJoin3.ref)
      probeJoin3.expectMessage(LobbyReferent(lobby.ref))
      probeJoin.expectMessage(LobbyInfoMessage(emptyLobby))
      probeJoin2.expectMessage(LobbyInfoMessage(emptyLobby))
      probeJoin3.expectMessage(LobbyInfoMessage(emptyLobby))
      //probe create room
      lobby ! CreateRoom(probeJoin.ref, RoomInfo(RoomBasicInfo(roomUsa, 0, 6), Set.empty, mapUsa))
      probeJoin2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      probeJoin3.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      //probe2 join
      lobby ! JoinTo(probeJoin2.ref, roomUsa)
      probeJoin3.expectNoMessage()
      //probe3 try join and receive error response
      lobby ! JoinTo(probeJoin3.ref, roomAmerica)
      probeJoin3.expectMessage(ErrorMessage("Room not found"))
    }
  }

  "StartGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyStart")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeStart")
      val player1: Player = Player("player1","red")
      val player2: Player = Player("player2","blue")
      val player1probe: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("playerJoin")
      val player2probe: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player2Join")
      val players = HashMap((player1->player1probe.ref),(player2->player2probe.ref))
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyReferent(lobby.ref))
      probeSub.expectMessageType[LobbyInfoMessage]
      lobby ! StartGame(RoomInfo(RoomBasicInfo(roomName, 4,4), Set.empty, mapName),
        players,
        HashSet.empty[ActorRef[PlayerMessage]])
      probeSub.expectMessage(LobbyInfoMessage(LobbyInfo(emptyRoomNameInfoList,Set(roomName),emptyStringList)))
    }
  }

  "EndGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyEnd")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameEnd")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeEnd")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,Set(roomName))
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyReferent(lobby.ref))
      probeSub.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! EndGame(roomName,game)
      probeSub.expectMessage(LobbyInfoMessage(nonEmptyLobby))
    }
  }

  "CloseGame to Lobby" should {
    "update Lobby info" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyClose")
      val game: ActorRef[GameMessage] = testKit.spawn(Behaviors.ignore[GameMessage], "GameClose")
      val probeSub: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeClose")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,Set(roomName))
      lobby ! Subscribe(probeSub.ref)
      probeSub.expectMessage(LobbyReferent(lobby.ref))
      probeSub.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! EndGame(roomName,game)
      probeSub.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      lobby ! GameClosed(roomName, List.empty[ActorRef[PlayerMessage]])
      probeSub.expectMessage(LobbyInfoMessage(nonEmptyLobby))
    }
  }

  "UpdateRoom to Lobby" should {
    "update the Room info and notify subs" in {
      val lobby: ActorRef[LobbyMessage] = testKit.spawn(LobbyManager(), "LobbyUpdate")
      val probeUpdate: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeUpdate")
      val probeUpdate2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("probeUpdate2")
      val emptyLobby = LobbyInfo(emptyRoomNameInfoList,emptyStringList,emptyStringList)
      val nonEmptyLobby = LobbyInfo(Set(RoomNameInfo(roomName,"0/4")),emptyStringList,emptyStringList)
      lobby ! Subscribe(probeUpdate.ref)
      probeUpdate.expectMessage(LobbyReferent(lobby.ref))
      lobby ! Subscribe(probeUpdate2.ref)
      probeUpdate2.expectMessage(LobbyReferent(lobby.ref))
      probeUpdate.expectMessage(LobbyInfoMessage(emptyLobby))
      probeUpdate2.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! CreateRoom(probeUpdate.ref, mapInfo)
      probeUpdate.expectMessageType[RoomReferent]
      probeUpdate.expectMessage(RoomInfoMessage(mapInfo))
      probeUpdate2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      lobby ! UpdateRoomInfo(mapInfo.basicInfo)
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
      val nonEmptyLobby = LobbyInfo(Set(RoomNameInfo(roomName,"0/4")),emptyStringList,emptyStringList)
      lobby ! Subscribe(probeEmpty.ref)
      probeEmpty.expectMessage(LobbyReferent(lobby.ref))
      lobby ! Subscribe(probeEmpty2.ref)
      probeEmpty2.expectMessage(LobbyReferent(lobby.ref))
      probeEmpty.expectMessage(LobbyInfoMessage(emptyLobby))
      probeEmpty2.expectMessage(LobbyInfoMessage(emptyLobby))
      lobby ! CreateRoom(probeEmpty.ref, mapInfo)
      probeEmpty.expectMessageType[RoomReferent]
      probeEmpty.expectMessage(RoomInfoMessage(mapInfo))
      probeEmpty2.expectMessage(LobbyInfoMessage(nonEmptyLobby))
      lobby ! EmptyRoom(mapInfo.basicInfo.name)
      probeEmpty.expectNoMessage()
      probeEmpty2.expectMessage(LobbyInfoMessage(emptyLobby))
    }
  }
}
