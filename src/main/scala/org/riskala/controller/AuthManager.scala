package org.riskala.controller

import java.io.InputStream

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.riskala.model.Account
import spray.json.{DefaultJsonProtocol, JsString, JsonParser, RootJsonFormat}

import scala.collection.immutable.HashMap
import pdi.jwt.{Jwt, JwtAlgorithm, JwtBase64, JwtOptions}
import argonaut.Argonaut._

import scala.util.{Success, Try}

case class Login(username: String, password: String)
case class Register(username: String, password: String, email: String)

object LoginJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val LoginFormats: RootJsonFormat[Login] = jsonFormat2(Login)
  implicit val RegisterFormats: RootJsonFormat[Register] = jsonFormat3(Register)
}

object AuthManager {
  private val secretKey = "PPSRiskala!"
  private val jwtAlgorithm = JwtAlgorithm.HS256

  private val stream: InputStream = getClass.getResourceAsStream("/account.txt")
  private val lines: String = scala.io.Source.fromInputStream( stream ).getLines().foldLeft("")(_+_)
  private val accountList: List[Account] = lines.decodeOption[List[Account]].getOrElse(List.empty)

  private var credential: HashMap[String,Account] = HashMap()
  accountList.foreach(acc=> credential = credential+(acc.username->acc))
  def login(l: Login): Option[String] = {
    credential get l.username flatMap(acc => if(acc.password == l.password) Some(genToken(l)) else None)
  }

  def register(r: Register): Option[String] = {
    if(!credential.isDefinedAt(r.username)) {
      credential = credential + (r.username -> Account(r.username,r.password,r.email))
      Some(genToken(Login(r.username,r.password)))
    } else {
      None
    }
  }

  private def genToken(l: Login): String = {
    import LoginJsonSupport._
    import spray.json._
    val claim = l.toJson.prettyPrint
    Jwt.encode(claim, secretKey, jwtAlgorithm)
  }

  def checkToken(token: String): Boolean = {
    Jwt.isValid(token, secretKey, Seq(jwtAlgorithm))
  }

  def getUser(token: String): Option[String] = {
    Jwt.decodeRawAll(token, secretKey, Seq(jwtAlgorithm)) match {
      case Success(tuple) => tuple match {
        case (header, claim, signature) =>
            Try(JsonParser(claim).convertTo[Login](LoginJsonSupport.LoginFormats)) match {
              case Success(login) => Some(login.username)
              case _ => None
            }
      }
      case _ => None
    }
  }
}

