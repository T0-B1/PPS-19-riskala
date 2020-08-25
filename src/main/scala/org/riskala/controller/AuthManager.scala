package org.riskala.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import scala.collection.immutable.HashMap
import pdi.jwt.{Jwt, JwtAlgorithm}

case class Login(username: String, password: String)
case class Register(username: String, password: String, email: String)
object LoginJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val LoginFormats = jsonFormat2(Login)
  implicit val RegisterFormats = jsonFormat3(Register)
}

object AuthManager {
  private val secretKey = "PPSRiskala!"
  private val jwtAlgorithm = JwtAlgorithm.HS256

  private var credential: HashMap[String,String] = HashMap("Test"->"1234","Giordo"->"1234","NarcAle"->"1234")
  def login(l: Login): Option[String] = {
    credential get l.username flatMap(psw => if(psw == l.password) Some(genToken(l)) else None)
  }

  def register(r: Register): Option[String] = {
    if(!credential.isDefinedAt(r.username)) {
      credential = credential + (r.username -> r.password)
      Some(genToken(Login(r.username,r.password)))
    } else {
      None
    }
  }

  private def genToken(l: Login): String = {
    import LoginJsonSupport._
    import spray.json._
    val claim = l.toJson.prettyPrint
    println(claim)
    Jwt.encode(claim, secretKey, jwtAlgorithm)
  }
}

