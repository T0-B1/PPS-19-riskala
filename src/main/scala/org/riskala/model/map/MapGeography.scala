package org.riskala.model.map

import argonaut.Argonaut._
import argonaut.CodecJson
import org.riskala.model.map.State.State
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Map structure #scala.js
 *
 * @param name           name of the map used in game.
 * @param regions        list of regions
 * @param states         list of state
 * @param bridges        list of bridges between states
 */
@JSExportTopLevel("MapGeography")
@JSExportAll
case class MapGeography(name:String,
                        regions: Set[Region],
                        states: Set[State],
                        bridges: Set[Bridge]) {

  /**
   * Defines all the neighbor of a state
   *
   * @param state the state of wich we need to know his neighbors
   * @return a list of state that are neighbor with our state
   */
  def getNeighbors(state: State): Set[State] = bridges collect {
    case Bridge(s1,s2,_) if s1 == state => s2
    case Bridge(s1,s2,_) if s2 == state => s1
  }

  /**
   * check if two state are neighbors
   *
   * @return true if state1 and state2 are neighbors
   */
  def areNeighbor(state1: State, state2: State): Boolean =
    bridges.exists(_ == Bridge(state1, state2, false))
  //IMPORTANT! Set.contains is abstract and depends on concrete implementation, had to use exists
}
object MapGeography {
  implicit def MapGeographyCodecJson: CodecJson[MapGeography] =
    casecodec4(MapGeography.apply,MapGeography.unapply)("name","regions","states","bridges")
}
