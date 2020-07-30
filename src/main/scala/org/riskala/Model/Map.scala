package org.riskala.Model

trait Map {
  val name: String
  val regions: List[Region]
  val states: List[State]
  val bridges: List[Bridge]

  def neighbor(state:State): List[State]
  def areNeighbor(state1:State, state2:State): Boolean
}
