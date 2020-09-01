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

  lobby ! Subscribe(probeSub.ref)
  probeSub.expectMessageType[PlayerMessage]
  lobby ! Logout(probeSub.ref)
  probeSub.expectNoMessage()

  lobby ! Subscribe(probeCreate.ref)
  lobby ! Subscribe(probeCreate2.ref)
  probeCreate.expectMessageType[PlayerMessage]
  probeCreate2.expectMessageType[PlayerMessage]
  lobby ! CreateRoom(probeCreate.ref, RoomInfo(RoomBasicInfo("Europa", 0, 4), ""))
  probeCreate.expectNoMessage()
  probeCreate2.expectMessageType[PlayerMessage]
  lobby ! Logout(probeCreate2.ref)

  println("FIN QUI TUTTO OK")

  lobby ! Subscribe(probeJoin.ref)
  probeJoin.expectMessageType[PlayerMessage]
  lobby ! Subscribe(probeJoin2.ref)
  probeJoin2.expectMessageType[PlayerMessage]
  lobby ! Subscribe(probeJoin3.ref)
  probeJoin3.expectMessageType[PlayerMessage]

  lobby ! CreateRoom(probeJoin.ref, RoomInfo(RoomBasicInfo("Usa", 0, 6), ""))
  probeJoin.expectNoMessage()
  //probeJoin2.expectMessageType[PlayerMessage]
  //probeJoin3.expectMessageType[PlayerMessage]


  lobby ! JoinTo(probeJoin2.ref, "Usa")
  probeJoin2.expectNoMessage()

  //probeJoin3.expectNoMessage()
  lobby ! JoinTo(probeJoin3.ref, "America")
  //probeJoin3.expectMessageType[PlayerMessage]

}
