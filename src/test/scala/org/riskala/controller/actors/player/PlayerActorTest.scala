package org.riskala.controller.actors.player

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ScalaTestWithActorTestKit}
import org.junit.runner.RunWith
import org.riskala.controller.actors.Messages.{GameMessage, LobbyMessage, RoomMessage}
import org.riskala.controller.actors.player.PlayerMessages.{GameReferent, RoomReferent}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
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
    val behaviorTestKit = BehaviorTestKit(LobbyPlayerBehavior("test", lobbyProbe.ref, null))

    "in the lobby" should {
      "go into a room upon receiving a RoomReferent" in {
        val roomProbe = testKit.createTestProbe[RoomMessage]()
        behaviorTestKit.run(RoomReferent(roomProbe.ref))
        val RoomBehaviourClassName = RoomPlayerBehavior.getClass.getSimpleName
        assert(behaviorTestKit.currentBehavior.toString
          .contains(RoomBehaviourClassName.substring(0, RoomBehaviourClassName.length - 1)))
      }
    }

    "in a room" should {
      "go into a game upon receiving a GameReferent" in {
        val gameProbe = testKit.createTestProbe[GameMessage]()
        behaviorTestKit.run(GameReferent(gameProbe.ref))
        val GameBehaviourClassName = GamePlayerBehavior.getClass.getSimpleName
        assert(behaviorTestKit.currentBehavior.toString
          .contains(GameBehaviourClassName.substring(0, GameBehaviourClassName.length - 1)))
      }
    }
  }

}
