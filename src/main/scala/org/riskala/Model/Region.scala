package org.riskala.Model

import org.riskala.Model.State.State


/** Region structure
 *
 * @param states       list of states
 * @param bonus        bonus
 */
case class Region(states: List[State], bonus: Int) {

  /**
   * check if the region contains the given state
   *
   * @param state     the state to check
   * @return true if it contains state*/
  def hasState(state: State): Boolean = states.contains(state)
}
