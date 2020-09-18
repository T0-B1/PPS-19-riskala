package org.riskala.modelToFix

import org.riskala.modelToFix.State.State
import argonaut.Argonaut._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Region structure
 *
 * @param states       list of states
 * @param bonus        bonus
 */
@JSExportTopLevel("Region")
@JSExportAll
case class Region(name: String, states: Set[State], bonus: Int) {

  /**
   * check if the region contains the given state
   *
   * @param state     the state to check
   * @return true if it contains state*/
  def hasState(state: State): Boolean = states.contains(state)
}
object Region {
  implicit def RegionCodecJson = casecodec3(Region.apply,Region.unapply)("name","states","bonus")
}