package org.riskala.Model

import argonaut.Argonaut._

/** Account structure
 *
 * @param username       username of user
 * @param password       password
 * @param email          email
 */
case class Account(username: String, password: String, email: String)
object Account {
  implicit def BridgeCodecJson =
    casecodec3(Account.apply,Account.unapply)("username","password","email")
}
