package org.riskala.utils

import java.io.FileNotFoundException
import java.util.Properties
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.reflect.ClassTag
import scala.concurrent.duration._
import scala.io.Source
import com.mashape.unirest.http.Unirest
import org.riskala.controller.auth.AuthManager

object Utils{

  val PROPS_PATH = "/config.properties"

  /**
   * Loads properties from resouce file
   *
   * @return A Properties object
   */
  def loadPropertiesFromResources(): Properties = {
    val properties: Properties = new Properties()
    val url = getClass.getResource(PROPS_PATH)
    if (url != null)
      properties.load(Source.fromURL(url).bufferedReader())
    else
      throw new FileNotFoundException(s"Properties file at path $PROPS_PATH cannot be loaded")
    properties
  }

  /**
   * Asks the akka system receptionist to find an actor
   *
   * @param key ServiceKey of the actor
   * @param system Akka actor system
   * @param timeout Timeout of the request
   * @tparam T Type of the ActorRef
   * @return An optional ActorRef
   */
  def askReceptionistToFind[T:ClassTag](key: String)(implicit system: ActorSystem[Nothing], timeout: Timeout = 3.seconds) : Set[ActorRef[T]] = {
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val serviceKey: ServiceKey[T] = ServiceKey[T](key)
    val receptionist: ActorRef[Receptionist.Command] = system.receptionist
    val response: Future[Receptionist.Listing] =
      receptionist ? (replyTo => Receptionist.find(serviceKey, replyTo))
    Await.result(response, 3.seconds).serviceInstances(serviceKey)
  }

  /**
   * Retrieves a random element from a set
   *
   * @param s A set
   * @tparam T Type of elements in the set
   * @return A random element
   */
  def randomSetElement[T](s: Set[T]): T = {
    val n = util.Random.nextInt(s.size)
    s.iterator.drop(n).next
  }

  /**
   * Sends an email to a user
   *
   * @param userName The username
   * @param gameName The game to which the notification refers
   */
  def sendUserTurnNotification(userName: String, gameName: String): Unit = {
    val email = AuthManager.getUserMail(userName)
    if(email.isDefined) {
      val props = loadPropertiesFromResources()
      val domain = props.get("mailgunDomain").toString
      val apiKey = props.get("mailgunKey").toString
      Unirest.post("https://api.mailgun.net/v3/" + domain + "/messages")
        .basicAuth("api", apiKey)
        .queryString("from", "Riskala Bot <mailgun@sandbox7d5c8d16a2274c5ab7e6825f4e7c7733.mailgun.org>")
        .queryString("to", email.get)
        .queryString("subject", "It's your turn!")
        .queryString("text", s"Hey $userName!\nIt's your turn to play, $gameName awaits you.")
        .asJson()
    }
  }

}


