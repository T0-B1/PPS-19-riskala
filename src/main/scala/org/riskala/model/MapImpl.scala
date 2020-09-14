package org.riskala.model

import org.riskala.model.State.State
import argonaut.Argonaut._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Map structure
 *
 * @param name           name of the map used in game.
 * @param regions        list of regions
 * @param states         list of state
 * @param bridges        list of bridges between states
 */
@JSExportTopLevel("MapImpl")
@JSExportAll
case class MapImpl(override val name:String,
                   override val regions: List[Region],
                   override val states: List[State],
                   override val bridges: List[Bridge]) extends Map {

  /*override def neighbor(state: State): List[State] = {
    bridges.filter(x => x.state1 == state || x.state2 == state)
      .map(b => if(b.state1==state)b.state2 else b.state1)
  }*/

  /**
   * Defines all the neighbor of a state
   *
   * @param state the state of wich we need to know his neighbors
   * @return a list of state that are neighbor with our state
   */
  override def getNeighbors(state: State): List[State] = bridges collect {
    case Bridge(s1,s2,_) if s1 == state => s2
    case Bridge(s1,s2,_) if s2 == state => s1
  }

  /**
   * check if two state are neighbors
   *
   * @return true if state1 and state2 are neighbors
   */
  override def areNeighbor(state1: State, state2: State): Boolean =
    bridges.contains(Bridge(state1,state2,false))
}
object MapImpl {
  implicit def MapCodecJson =
    casecodec4(MapImpl.apply,MapImpl.unapply)("name","regions","states","bridges")
}
