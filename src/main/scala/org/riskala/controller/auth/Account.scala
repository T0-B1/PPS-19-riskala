package org.riskala.controller.auth

import argonaut.Argonaut._
import argonaut.CodecJson
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
 * Account structure #scala.js
 *
 * @param username       username of user
 * @param password       password
 * @param email          email
 */
@JSExportTopLevel("Account")
@JSExportAll
case class Account(username: String, password: String, email: String)
object Account {
  implicit def BridgeCodecJson: CodecJson[Account] =
    casecodec3(Account.apply,Account.unapply)("username","password","email")
}
