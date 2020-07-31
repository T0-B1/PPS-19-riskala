package org.riskala.Model

import org.riskala.Model.State.State

case class Region(states: List[State],
                  bonus: Int) {

  def hasState(state: State): Boolean = states.contains(state)
}
