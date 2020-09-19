package org.riskala.model

import argonaut.Argonaut._
import org.riskala.model.State.State

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
 * Represents the current state of a state during a game
 *
 * @param owner     the player who owns the state
 * @param troops    the number of troops to deploy
 * */
@JSExportTopLevel("PlayerState")
@JSExportAll
case class PlayerState(state: State, owner: Player, troops: Int)

object PlayerState {
  implicit def PlayerStateCodecJson =
    casecodec3(PlayerState.apply, PlayerState.unapply)("name", "owner", "troops")

  def updatePlayerStates(state: PlayerState, set: Set[PlayerState]): Set[PlayerState] = {
    set.filterNot(p => p.state.equals(state.state)) + state
  }

  def getPlayerState(state: State, set: Set[PlayerState]): Option[PlayerState] = {
    set.collectFirst({ case p if p.state.equals(state) => p })
  }
}
