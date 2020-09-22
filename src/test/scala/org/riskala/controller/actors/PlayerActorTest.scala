package org.riskala.controller.actors

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ScalaTestWithActorTestKit}
import org.riskala.model.ModelMessages.{GameMessage, LobbyMessage, RoomMessage}
import org.scalatest.wordspec.AnyWordSpecLike
import org.junit.runner.RunWith
import org.riskala.controller.actors.player.{PlayerActor, PlayerGameBehavior, PlayerLobbyBehavior, PlayerRoomBehavior}
import org.riskala.controller.actors.player.PlayerMessages.{GameReferent, RoomReferent}
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlayerActorTest extends ScalaTestWithActorTestKit with AnyWordSpecLike with Matchers {

  "A PlayerActor" when {
    "spawned" must {
      "seek for the lobby" in {
        val behaviorTestKit = BehaviorTestKit(PlayerActor("test", null))
        behaviorTestKit.receptionistInbox().receiveAll().size shouldEqual 1
      }
    }
  }

  "A PlayerActor" when {
    val lobbyProbe = testKit.createTestProbe[LobbyMessage]()
    val behaviorTestKit = BehaviorTestKit(PlayerLobbyBehavior("test", lobbyProbe.ref, null))

    "in the lobby" should {
      "go into a room upon receiving a RoomReferent" in {
        val roomProbe = testKit.createTestProbe[RoomMessage]()
        behaviorTestKit.run(RoomReferent(roomProbe.ref))
        assert(behaviorTestKit.currentBehavior.toString.contains("PlayerRoomBehavior"))
        val RoomBehaviourClassName = PlayerRoomBehavior.getClass.getSimpleName
        assert(behaviorTestKit.currentBehavior.toString
          .contains(RoomBehaviourClassName.substring(0, RoomBehaviourClassName.size - 1)))
      }
    }

    "in a room" should {
      "go into a game upon receiving a GameReferent" in {
        val gameProbe = testKit.createTestProbe[GameMessage]()
        behaviorTestKit.run(GameReferent(gameProbe.ref))
        val GameBehaviourClassName = PlayerGameBehavior.getClass.getSimpleName
        assert(behaviorTestKit.currentBehavior.toString
          .contains(GameBehaviourClassName.substring(0, GameBehaviourClassName.size - 1)))
      }
    }
  }

}
