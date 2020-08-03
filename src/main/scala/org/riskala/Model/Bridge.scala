package org.riskala.Model

import org.riskala.Model.State.State

/** Bridge implementation
 *
 * @param state1         first state
 * @param state2         second state
 * @param userCreated    true if bridge is defined by user
 */
case class Bridge(state1: State,
                  state2: State,
                  userCreated: Boolean) {

  /**
   * check if the object is a bridge and if this bridge link the same states
   *
   * @return true if equal
   */
  override def equals(obj: Any): Boolean = obj match {
    case Bridge(s1,s2,_) => (s1 == state1 && s2 == state2) || (s2 == state1 && s1 == state2)
    case _ => false
  }
}
