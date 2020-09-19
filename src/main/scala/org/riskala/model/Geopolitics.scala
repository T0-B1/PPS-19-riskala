package org.riskala.model

import org.riskala.model.State.State

case class Geopolitics(states: Set[PlayerState]) {

  implicit def states2geopolitics(states: Set[PlayerState]): Geopolitics = Geopolitics(states)

  def updateGeopolitics(state: PlayerState): Geopolitics = {
    Geopolitics(states.filterNot(p => p.state.equals(state.state)) + state)
  }

  def getPlayerState(state: State): Option[PlayerState] = {
    states.collectFirst({ case p if p.state.equals(state) => p })
  }

  def updateStateOwner(state: State, newOwner: Player): Geopolitics = {
    val ps = getPlayerState(state)
    if(ps.isEmpty)
      this
    else
      updateGeopolitics(ps.get.copy(owner = newOwner))
  }

  def modifyStateTroops(state: State, troopsDelta: Int): Geopolitics = {
    alterStateTroops(state, troopsDelta, additive = true)
  }

  def setStateTroops(state: State, troops: Int): Geopolitics = {
    alterStateTroops(state, troops, additive = false)
  }

  private def alterStateTroops(state: State, troops: Int, additive: Boolean): Geopolitics = {
    val ps = getPlayerState(state)
    if(ps.isEmpty)
      this
    else {
      var t = troops
      if(additive)
        t = ps.get.troops + troops
      updateGeopolitics(ps.get.copy(troops = t))
    }
  }

}


object Geopolitics {

  implicit def states2geopolitics(states: Set[PlayerState]): Geopolitics = Geopolitics(states)

  implicit def geopolitics2states(geopolitics: Geopolitics): Set[PlayerState] = geopolitics.states
  
}
