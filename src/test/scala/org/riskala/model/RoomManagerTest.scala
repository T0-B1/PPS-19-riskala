package org.riskala.model

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import org.riskala.controller.actors.PlayerMessages.{PlayerMessage, RoomInfoMessage}
import org.riskala.model.room.RoomManager
import org.riskala.model.room.RoomMessages._
import org.riskala.model.ModelMessages._
import org.riskala.model.lobby.LobbyMessages.{Subscribe, UpdateRoomInfo}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpec

class RoomManagerTest extends AnyWordSpec with BeforeAndAfterAll {
  val testKit: ActorTestKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val roomInfo: RoomInfo = RoomInfo(RoomBasicInfo("Europa", 0, 4), "")

  "Join to room" should {
    "give roomInfo" in {
      val JoinLobby: TestProbe[LobbyMessage] = testKit.createTestProbe[LobbyMessage]("JoinLobby")
      val room: ActorRef[RoomMessage] = testKit.spawn(RoomManager(roomInfo, JoinLobby.ref), "RoomJoin")
      val player: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("playerJoin")
      val player2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player2Join")

      room ! Join(player.ref)
      room ! Join(player2.ref)
      player.expectMessage(RoomInfoMessage(roomInfo))
      player2.expectMessage(RoomInfoMessage(roomInfo))
    }
  }

  "Leave room" should {
    "remove user from roomUpdateInfo and close behavior" in {
      val leaveLobby: TestProbe[LobbyMessage] = testKit.createTestProbe[LobbyMessage]("leaveLobby")
      val room: ActorRef[RoomMessage] = testKit.spawn(RoomManager(roomInfo, leaveLobby.ref), "RoomLeave")
      val playerLeave: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("playerLeave")
      val player2Leave: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player2Leave")

      room ! Join(playerLeave.ref)
      playerLeave.expectMessage(RoomInfoMessage(roomInfo))
      room ! Join(player2Leave.ref)
      player2Leave.expectMessage(RoomInfoMessage(roomInfo))
      room ! Leave(playerLeave.ref)
      playerLeave.expectNoMessage()
      leaveLobby.expectMessage(Subscribe(playerLeave.ref))
    }
  }

  "Ready in room" should {
    "give roomInfo" in {
      val readyLobby: TestProbe[LobbyMessage] = testKit.createTestProbe[LobbyMessage]("ReadyLobby")
      val room: ActorRef[RoomMessage] = testKit.spawn(RoomManager(roomInfo, readyLobby.ref), "RoomReady")
      val playerReady: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("playerReady")
      val player: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player")

      room ! Join(playerReady.ref)
      playerReady.expectMessage(RoomInfoMessage(roomInfo))
      room ! Join(player.ref)
      player.expectMessage(RoomInfoMessage(roomInfo))

      room ! Ready("playerReady", playerReady.ref)
      playerReady.expectMessage(RoomInfoMessage(RoomInfo(RoomBasicInfo("Europa", 1, 4), "")))
      player.expectMessage(RoomInfoMessage(RoomInfo(RoomBasicInfo("Europa", 1, 4), "")))

      readyLobby.expectMessage(UpdateRoomInfo(RoomBasicInfo("Europa", 1, 4)))
    }
  }

  "UnReady in room" should {
    "remove from roomInfo" in {
      val unReadyLobby: TestProbe[LobbyMessage] = testKit.createTestProbe[LobbyMessage]("unReadyLobby")
      val room: ActorRef[RoomMessage] = testKit.spawn(RoomManager(roomInfo, unReadyLobby.ref), "RoomUnReady")
      val playerUnReady: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("playerUnReady")
      val player_Join: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player_Join")

      room ! Join(playerUnReady.ref)
      playerUnReady.expectMessageType[PlayerMessage]
      room ! Join(player_Join.ref)
      player_Join.expectMessageType[PlayerMessage]

      room ! Ready("playerUnReady", playerUnReady.ref)
      playerUnReady.expectMessageType[PlayerMessage]
      player_Join.expectMessageType[PlayerMessage]

      room ! UnReady("playerUnReady", playerUnReady.ref)
      player_Join.expectMessageType[PlayerMessage]
      playerUnReady.expectMessageType[PlayerMessage]
    }
  }

  "Everybody ready in room" should {
    "start game" in {
      val lobby: TestProbe[LobbyMessage] = testKit.createTestProbe[LobbyMessage]("LobbyReady")
      val room: ActorRef[RoomMessage] = testKit.spawn(RoomManager(roomInfo, lobby.ref), "RoomEveryReady")
      val player: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player")
      val player2: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player2")
      val player3: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player3")
      val player4: TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player4")

      room ! Join(player.ref)
      player.expectMessageType[PlayerMessage]
      room ! Join(player2.ref)
      player2.expectMessageType[PlayerMessage]
      room ! Join(player3.ref)
      player3.expectMessageType[PlayerMessage]
      room ! Join(player4.ref)
      player4.expectMessageType[PlayerMessage]

      room ! Ready("NarcAle", player.ref)
      player.expectMessageType[PlayerMessage]
      player2.expectMessageType[PlayerMessage]
      player3.expectMessageType[PlayerMessage]
      player4.expectMessageType[PlayerMessage]

      room ! Ready("Giordo", player2.ref)
      player.expectMessageType[PlayerMessage]
      player2.expectMessageType[PlayerMessage]
      player3.expectMessageType[PlayerMessage]
      player4.expectMessageType[PlayerMessage]

      room ! Ready("Marto", player3.ref)
      player.expectMessageType[PlayerMessage]
      player2.expectMessageType[PlayerMessage]
      player3.expectMessageType[PlayerMessage]
      player4.expectMessageType[PlayerMessage]

      room ! Ready("Luca", player4.ref)
      player.expectMessageType[PlayerMessage]
      player2.expectMessageType[PlayerMessage]
      player3.expectMessageType[PlayerMessage]
      player4.expectMessageType[PlayerMessage]

      lobby.expectMessageType[LobbyMessage]
    }
  }

  "Everybody logout to a room" should{
    "remove form roomInfo" in {
      val lobby: TestProbe[LobbyMessage] = testKit.createTestProbe[LobbyMessage]("LobbyDead")
      val room: ActorRef[RoomMessage] = testKit.spawn(RoomManager(roomInfo, lobby.ref), "RoomLogout")
      val player_ : TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player_")
      val player2_ : TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player2_")
      val player3_ : TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player3_")
      val player4_ : TestProbe[PlayerMessage] = testKit.createTestProbe[PlayerMessage]("player4_")

      room ! Join(player_.ref)
      player_.expectMessageType[PlayerMessage]
      room ! Join(player2_.ref)
      player2_.expectMessageType[PlayerMessage]
      room ! Join(player3_.ref)
      player3_.expectMessageType[PlayerMessage]
      room ! Join(player4_.ref)
      player4_.expectMessageType[PlayerMessage]

      room ! Ready("NarcAle", player_.ref)
      player_.expectMessageType[PlayerMessage]
      player2_.expectMessageType[PlayerMessage]
      player3_.expectMessageType[PlayerMessage]
      player4_.expectMessageType[PlayerMessage]

      room ! Ready("Giordo", player2_.ref)
      player_.expectMessageType[PlayerMessage]
      player2_.expectMessageType[PlayerMessage]
      player3_.expectMessageType[PlayerMessage]
      player4_.expectMessageType[PlayerMessage]

      room ! Ready("Marto", player3_.ref)
      player_.expectMessageType[PlayerMessage]
      player2_.expectMessageType[PlayerMessage]
      player3_.expectMessageType[PlayerMessage]
      player4_.expectMessageType[PlayerMessage]

      room ! Ready("Luca", player4_.ref)
      player_.expectMessageType[PlayerMessage]
      player2_.expectMessageType[PlayerMessage]
      player3_.expectMessageType[PlayerMessage]
      player4_.expectMessageType[PlayerMessage]

      lobby.expectMessageType[LobbyMessage]

      room ! Logout(player_.ref)
      player_.expectNoMessage()
      room ! Logout(player2_.ref)
      player2_.expectNoMessage()
      room ! Logout(player3_.ref)
      player3_.expectNoMessage()
      room ! Logout(player4_.ref)
      player4_.expectNoMessage()

      lobby.expectMessageType[LobbyMessage]
    }
  }

}
