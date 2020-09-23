package org.riskala.controller.auth

import java.io.InputStream
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import argonaut.Argonaut._
import pdi.jwt.{Jwt, JwtAlgorithm}
import spray.json.{DefaultJsonProtocol, JsonParser, RootJsonFormat}
import scala.collection.immutable.HashMap
import scala.util.{Success, Try}

case class LoginData(username: String, password: String)

case class RegistrationData(username: String, password: String, email: String)

object LoginJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val LoginFormats: RootJsonFormat[LoginData] = jsonFormat2(LoginData)
  implicit val RegisterFormats: RootJsonFormat[RegistrationData] = jsonFormat3(RegistrationData)
}

object AuthManager {
  private val secretKey = "PPSRiskala!"
  private val jwtAlgorithm = JwtAlgorithm.HS256

  private val stream: InputStream = getClass.getResourceAsStream("/account.txt")
  private val lines: String = scala.io.Source.fromInputStream( stream ).getLines().foldLeft("")(_+_)
  private val accountList: List[Account] = lines.decodeOption[List[Account]].getOrElse(List.empty)

  private var credential: Map[String,Account] = HashMap()
  accountList.foreach(acc=> credential = credential+(acc.username->acc))

  /**
   * Logs a user returning the token if successful
   *
   * @param l Login data of a user
   * @return An optional token
   */
  def login(l: LoginData): Option[String] = {
    credential get l.username flatMap(acc => if(acc.password == l.password) Some(genToken(l)) else None)
  }

  /**
   * Registers a user returning the token if successful
   *
   * @param r Registration data of a user
   * @return An optional token
   */
  def register(r: RegistrationData): Option[String] = {
    if(!credential.isDefinedAt(r.username)) {
      credential = credential + (r.username -> Account(r.username,r.password,r.email))
      Some(genToken(LoginData(r.username,r.password)))
    } else {
      None
    }
  }

  /**
   * Generates a token
   *
   * @param l User credentials
   * */
  private def genToken(l: LoginData): String = {
    import LoginJsonSupport._
    import spray.json._
    val claim = l.toJson.prettyPrint
    Jwt.encode(claim, secretKey, jwtAlgorithm)
  }

  /**
   * Checks the validity of a token
   *
   * @param token A token
   * @return If token is valid
   */
  def checkToken(token: String): Boolean = {
    Jwt.isValid(token, secretKey, Seq(jwtAlgorithm))
  }

  /**
   * Given a token, the associated username is returned if present
   * @param token A token
   * @return An optional username
   */
  def getUserName(token: String): Option[String] = {
    Jwt.decodeRawAll(token, secretKey, Seq(jwtAlgorithm)) match {
      case Success(tuple) => tuple match {
        case (_, claim, _) =>
            Try(JsonParser(claim).convertTo[LoginData](LoginJsonSupport.LoginFormats)) match {
              case Success(login) => Some(login.username)
              case _ => None
            }
      }
      case _ => None
    }
  }

  /**
   * Given a username, the email address is returned
   *
   * @param userName A username
   * @return An optional email address
   */
  def getUserMail(userName: String): Option[String] = {
    credential.get(userName).map(user => user.email)
  }
}

