package org.riskala.Model

trait Region {
  val states: List[State]
  val bonus: Int

  def belongs(state:State): Boolean
}
