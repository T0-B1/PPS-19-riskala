package org.riskala.model

import org.riskala.model.State.State
import argonaut.Argonaut._
import argonaut.CodecJson

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
 * Region structure #scala.js
 *
 * @param states       list of states of the region
 * @param bonus        bonus in terms of deployable troops
 */
@JSExportTopLevel("Region")
@JSExportAll
case class Region(name: String, states: Set[State], bonus: Int) {

  /**
   * Checks if the region contains the given state
   *
   * @param state     the state to check
   * @return true if it contains state*/
  def hasState(state: State): Boolean = states.contains(state)
}
object Region {
  implicit def RegionCodecJson: CodecJson[Region] =
    casecodec3(Region.apply,Region.unapply)("name","states","bonus")
}