package org.riskala.Model

case class StateImpl(override val name: String) extends State {

  override def equals(obj: Any): Boolean = obj match {
    case impl: StateImpl => impl.name equals name
    case _ => false
  }
}
