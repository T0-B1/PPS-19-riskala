package org.riskala.model.map

import argonaut.Argonaut._
import argonaut.CodecJson
import org.riskala.model.Player
import org.riskala.model.map.State.State
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
 * Represents the current state of a state during a game #scala.js
 *
 * @param owner     the player who owns the state
 * @param troops    the number of troops to deploy
 * */
@JSExportTopLevel("PlayerState")
@JSExportAll
case class PlayerState(state: State, owner: Player, troops: Int)

object PlayerState {

  implicit def PlayerStateCodecJson: CodecJson[PlayerState] =
    casecodec3(PlayerState.apply, PlayerState.unapply)("name", "owner", "troops")

}


