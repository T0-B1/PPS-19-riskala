package org.riskala.Model

import argonaut.Argonaut._

/** Playes structure
 *
 * @param nickname       nickname of user
 * @param color          color of user
 */
case class Player(nickname: String, color: String)
object Player {
  implicit def PlayerCodecJson =
    casecodec2(Player.apply, Player.unapply)("nickname","color")
}
