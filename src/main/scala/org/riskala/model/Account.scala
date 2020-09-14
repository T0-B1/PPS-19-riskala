package org.riskala.model

import argonaut.Argonaut._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Account structure
 *
 * @param username       username of user
 * @param password       password
 * @param email          email
 */
@JSExportTopLevel("Account")
@JSExportAll
case class Account(username: String, password: String, email: String)
object Account {
  implicit def BridgeCodecJson =
    casecodec3(Account.apply,Account.unapply)("username","password","email")
}
