package org.riskala.Model

case class RegionImpl(override val states: List[State],
                      override val bonus: Int) extends Region {

  override def belongs(state: State): Boolean = states.contains(state)
}
