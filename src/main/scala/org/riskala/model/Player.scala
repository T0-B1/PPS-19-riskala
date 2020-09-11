package org.riskala.model

import argonaut.Argonaut._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Playes structure
 *
 * @param nickname       nickname of user
 * @param color          color of user
 */
@JSExportTopLevel("Player")
@JSExportAll
case class Player(nickname: String, color: String)
object Player {
  implicit def PlayerCodecJson =
    casecodec2(Player.apply, Player.unapply)("nickname","color")
}
