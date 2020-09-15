package org.riskala.model

import org.riskala.model.State.State

object Objectives {

  sealed trait Objective {
    val info: String
    val states: Seq[State]
  }

  final case class WholeMap(override val states: Seq[State]) extends Objective {
    override val info: String = "Conquer all the states"
  }

}
