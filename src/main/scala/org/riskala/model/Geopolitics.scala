package org.riskala.model

import org.riskala.model.State.State

object Geopolitics {

  def updateGeopolitics(state: PlayerState, set: Set[PlayerState]): Set[PlayerState] = {
    set.filterNot(p => p.state.equals(state.state)) + state
  }

  def getPlayerState(state: State, set: Set[PlayerState]): Option[PlayerState] = {
    set.collectFirst({ case p if p.state.equals(state) => p })
  }

}
