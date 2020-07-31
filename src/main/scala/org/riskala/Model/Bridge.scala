package org.riskala.Model

import org.riskala.Model.State.State

case class Bridge(state1: State,
                  state2: State,
                  userCreated: Boolean) {

  override def equals(obj: Any): Boolean = obj match {
    case Bridge(s1,s2,_) => (s1 == state1 && s2 == state2) || (s2 == state1 && s1 == state2)
    case _ => false
  }
}
