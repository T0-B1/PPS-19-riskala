package org.riskala.Model

case class BridgeImpl(override val state1: State,
                      override val state2: State,
                      override val userCreated: Boolean) extends Bridge {

  override def equals(obj: Any): Boolean = obj match {
    case BridgeImpl(s1,s2,_) => (s1 == state1 && s2 == state2) || (s2 == state1 && s1 == state2)
    case _ => false
  }
}
