package org.riskala.model

import org.riskala.model.State.State

case class Geopolitics(states: Set[PlayerState]) {

  implicit def states2geopolitics(states: Set[PlayerState]): Geopolitics = Geopolitics(states)

  def updateGeopolitics(state: PlayerState): Geopolitics = {
    Geopolitics(states.filterNot(p => p.state.equals(state.state)) + state)
  }

  def getPlayerStateByName(state: State): Option[PlayerState] = {
    states.collectFirst({ case p if p.state.equals(state) => p })
  }

  def getStatesOfPlayer(player: Player): Set[State] = {
    states.collect({case ps if ps.owner.equals(player) => ps.state})
  }

  def updateStateOwner(state: State, newOwner: Player): Geopolitics = {
    getPlayerStateByName(state).fold(this)(ps=>updateGeopolitics(ps.copy(owner = newOwner)))
  }

  def modifyStateTroops(state: State, troopsDelta: Int): Geopolitics = {
    alterStateTroops(state, troopsDelta, additive = true)
  }

  def setStateTroops(state: State, troops: Int): Geopolitics = {
    alterStateTroops(state, troops, additive = false)
  }

  private def alterStateTroops(state: State, troops: Int, additive: Boolean): Geopolitics = {
    getPlayerStateByName(state).fold(this)(ps=>{
      updateGeopolitics(ps.copy(troops = if(additive) ps.troops + troops else troops))
    })
  }

}


object Geopolitics {

  implicit def states2geopolitics(states: Set[PlayerState]): Geopolitics = Geopolitics(states)

  implicit def geopolitics2states(geopolitics: Geopolitics): Set[PlayerState] = geopolitics.states

}
