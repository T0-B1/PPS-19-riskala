package org.riskala.Model

import org.riskala.Model.State.State

object State {
  type State = String
}

trait Map {
  val name: String
  val regions: List[Region]
  val states: List[State]
  val bridges: List[Bridge]

  /**
   * Defines all the neighbor of a state
   *
   * @param state the state of wich we need to know his neighbors
   * @return a list of state that are neighbor with our state
   */
  def getNeighbors(state:State): List[State]

  /**
   * check if two state are neighbors
   *
   * @return true if state1 and state2 are neighbors
   */
  def areNeighbor(state1:State, state2:State): Boolean
}
