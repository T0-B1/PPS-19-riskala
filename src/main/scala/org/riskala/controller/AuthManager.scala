package org.riskala.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import scala.collection.immutable.HashMap

object AuthManager {
  private var credential: HashMap[String,String] = HashMap("Test"->"1234")
  def login(l: Login): Option[String] = {
    credential get l.username flatMap(psw => if(psw == l.password) Some("token123ABC") else None)
  }

  def register(): Option[String] = {
    None
  }
}

case class Login(username: String, password: String)
object LoginJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val LoginFormats = jsonFormat2(Login)
}