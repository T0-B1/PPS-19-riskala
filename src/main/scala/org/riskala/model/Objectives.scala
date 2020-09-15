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

  final case class TwoRegions(override val states: Seq[State],
                              private val region1Name: String,
                              private val region2Name: String) extends Objective {
    override val info: String = s"Conquer the states of region $region1Name and $region2Name"
  }

  def generateObjective(map: Map): Objective = {
    val rng = scala.util.Random
    rng.nextInt(2) match {
      case 0 => WholeMap(map.states)
      case 1 => WholeMap(map.states) //TODO replace with TwoRegions
    }
  }
}
