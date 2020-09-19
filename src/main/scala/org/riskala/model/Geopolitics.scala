package org.riskala.model

import org.riskala.model.State.State

object Geopolitics {

  def updateGeopolitics(state: PlayerState, geopolitics: Set[PlayerState]): Set[PlayerState] = {
    geopolitics.filterNot(p => p.state.equals(state.state)) + state
  }

  def getPlayerState(state: State, geopolitics: Set[PlayerState]): Option[PlayerState] = {
    geopolitics.collectFirst({ case p if p.state.equals(state) => p })
  }

  def updateStateOwner(state: State, newOwner: Player, geopolitics: Set[PlayerState]) = {
    val ps = getPlayerState(state, geopolitics)
    if(ps.isEmpty)
      geopolitics
    else
      updateGeopolitics(ps.get.copy(owner = newOwner), geopolitics)
  }

  def modifyStateTroops(state: State, troopsDelta: Int, geopolitics: Set[PlayerState]) = {
    val ps = getPlayerState(state, geopolitics)
    if(ps.isEmpty)
      geopolitics
    else
      updateGeopolitics(ps.get.copy(troops = ps.get.troops + troopsDelta), geopolitics)
  }

}
