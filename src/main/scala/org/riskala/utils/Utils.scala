package org.riskala.utils

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.reflect.ClassTag
import scala.concurrent.duration._

object Utils{

  def askReceptionistToFind[T:ClassTag](key: String)(implicit system: ActorSystem[Nothing], timeout: Timeout = 3.seconds) : Set[ActorRef[T]] = {
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val serviceKey: ServiceKey[T] = ServiceKey[T](key)
    val receptionist: ActorRef[Receptionist.Command] = system.receptionist
    val response: Future[Receptionist.Listing] =
      receptionist.ask(replyTo => Receptionist.find(serviceKey, replyTo))
    Await.result(response, 3.seconds).serviceInstances(serviceKey)
  }

}
