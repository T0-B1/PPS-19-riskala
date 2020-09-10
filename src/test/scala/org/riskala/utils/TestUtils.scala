package org.riskala.utils

import java.io.FileNotFoundException
import java.util.Properties

import scala.io.Source

object TestUtils {

  val PROPS_PATH = "/test.properties"

  def loadPropertiesFromResources(): Properties = {
    val properties: Properties = new Properties()
    val url = getClass.getResource(PROPS_PATH)
    if (url != null)
      properties.load(Source.fromURL(url).bufferedReader())
    else
      throw new FileNotFoundException(s"Properties file at path $PROPS_PATH cannot be loaded");
    properties
  }

}

import akka.actor.{ActorSystem, Scheduler}
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, ActorTestKitBase}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.Suite
import akka.util.Timeout
import akka.actor.typed.scaladsl.adapter._

trait ScalatestTypedActorHttpRoute extends ScalatestRouteTest { this: Suite =>
  import akka.actor.typed.scaladsl.adapter._

  var typedTestKit:ActorTestKit = _ //val init causes createActorSystem() to cause NPE when typedTestKit.system is called in createActorSystem().
  implicit def timeout: Timeout = typedTestKit.timeout
  implicit def scheduler = typedTestKit.system.classicSystem.scheduler

  protected override def createActorSystem(): ActorSystem = {
    typedTestKit = ActorTestKit(ActorTestKitBase.testNameFromCallStack())
    typedTestKit.system.classicSystem
  }

  override def cleanUp(): Unit = {
    super.cleanUp()
    typedTestKit.shutdownTestKit()
  }
}
