package org.riskala.model

import org.riskala.model.State.State

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("State")
object State {
  type State = String
}

trait Map {
  val name: String
  val regions: Set[Region]
  val states: Set[State]
  val bridges: Set[Bridge]

  /**
   * Defines all the neighbor of a state
   *
   * @param state the state of wich we need to know his neighbors
   * @return a list of state that are neighbor with our state
   */
  def getNeighbors(state:State): Set[State]

  /**
   * check if two state are neighbors
   *
   * @return true if state1 and state2 are neighbors
   */
  def areNeighbor(state1:State, state2:State): Boolean
}
