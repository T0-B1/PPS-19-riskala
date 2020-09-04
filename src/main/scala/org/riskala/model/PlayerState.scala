package org.riskala.model

import argonaut.Argonaut._

/**
 * Represents the current state of a state during a game
 *
 * @param owner     the player who owns the state
 * @param troops    the number of troops to deploy
 * */
case class PlayerState(owner: Player, troops: Int)
object PlayerState {
  implicit def PlayerStateCodecJson =
    casecodec2(PlayerState.apply, PlayerState.unapply)("owner","troops")
}
