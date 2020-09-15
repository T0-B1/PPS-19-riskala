package org.riskala.model

import org.riskala.model.State.State

object Objectives {

  sealed trait Objective {
    val info: String
    val states: Seq[State]
  }

}
