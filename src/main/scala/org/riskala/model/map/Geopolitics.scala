package org.riskala.model.map

import org.riskala.model.Player
import org.riskala.model.map.State.State

/**
 * State of the map wrt the owners of the state and the troops stationed in every state
 * #scala.js
 *
 * @param states The geographical states that compose the scenario
 */
case class Geopolitics(states: Set[PlayerState]) {

  implicit def states2geopolitics(states: Set[PlayerState]): Geopolitics = Geopolitics(states)

  /**
   * Update the state of a PlayerState inside the global geopolitics
   *
   * @param state New state
   * @return New geopolitics
   */
  def updatePlayerState(state: PlayerState): Geopolitics = {
    Geopolitics(states.filterNot(p => p.state.equals(state.state)) + state)
  }

  /**
   * Returns a PlayerState given a state name
   * @param state State name
   * @return PlayerState
   */
  def getPlayerStateByName(state: State): Option[PlayerState] = {
    states.collectFirst({ case p if p.state.equals(state) => p })
  }

  /**
   * Returns all the states of a player
   * @param player A player
   * @return His states
   */
  def getStatesOfPlayer(player: Player): Set[State] = {
    states.collect({case ps if ps.owner.equals(player) => ps.state})
  }

  /**
   * Updates the owner of a state
   * @param state State to own
   * @param newOwner New owner
   * @return New geopolitics
   */
  def updateStateOwner(state: State, newOwner: Player): Geopolitics = {
    getPlayerStateByName(state).fold(this)(ps=>updatePlayerState(ps.copy(owner = newOwner)))
  }

  /**
   * Modifies the amount of troops in a state
   *
   * @param state A state
   * @param troopsDelta Increment or decrement in the number of troops mobilized
   * @return New geopolitics
   */
  def modifyStateTroops(state: State, troopsDelta: Int): Geopolitics = {
    alterStateTroops(state, troopsDelta, additive = true)
  }

  /**
   * Sets the amount of troops in a state
   *
   * @param state A state
   * @param troops The number of troops mobilized
   * @return New geopolitics
   */
  def setStateTroops(state: State, troops: Int): Geopolitics = {
    alterStateTroops(state, troops, additive = false)
  }
  
  private def alterStateTroops(state: State, troops: Int, additive: Boolean): Geopolitics = {
    getPlayerStateByName(state).fold(this)(ps=>{
      updatePlayerState(ps.copy(troops = if(additive) ps.troops + troops else troops))
    })
  }

}


object Geopolitics {

  implicit def states2geopolitics(states: Set[PlayerState]): Geopolitics = Geopolitics(states)

  implicit def geopolitics2states(geopolitics: Geopolitics): Set[PlayerState] = geopolitics.states

}
