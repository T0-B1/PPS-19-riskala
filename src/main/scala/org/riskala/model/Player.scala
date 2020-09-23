package org.riskala.model

import argonaut.Argonaut._
import argonaut.CodecJson
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
 * Player structure #scala.js
 *
 * @param nickname       nickname of user
 * @param color          color of user
 */
@JSExportTopLevel("Player")
@JSExportAll
case class Player(nickname: String, color: String){

  override def equals(obj: Any): JsonBoolean = obj match {
    case p: Player => p.nickname==nickname
    case _ => false
  }

}

object Player {

  implicit def PlayerCodecJson: CodecJson[Player] =
    casecodec2(Player.apply, Player.unapply)("nickname","color")

}
