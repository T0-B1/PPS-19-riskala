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
    alterStateTroops(state, troopsDelta, geopolitics, true)
  }

  def setStateTroops(state: State, troops: Int, geopolitics: Set[PlayerState]) = {
    alterStateTroops(state, troops, geopolitics, false)
  }

  private def alterStateTroops(state: State, troops: Int, geopolitics: Set[PlayerState], additive: Boolean) = {
    val ps = getPlayerState(state, geopolitics)
    if(ps.isEmpty)
      geopolitics
    else {
      var t = troops
      if(additive)
        t = ps.get.troops + troops
      updateGeopolitics(ps.get.copy(troops = t), geopolitics)
    }
  }

}
