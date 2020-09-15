package org.riskala.model

import org.riskala.model.State.State

object Objectives {

  final case class Objective(states: Seq[State], info: String)

  def generateRandomObjective(map: Map, numberOfPlayer: Int): Objective = {
    numberOfPlayer match {
      case _ if numberOfPlayer < 4 => Objective(map.states,"Conquer all states")
      case _ =>
        /*val rng = scala.util.Random
        rng.nextInt(2) match {
          case 0 => Objective(map.states)
          case 1 => Objective(map.states)
        }*/
        Objective(map.states,"Conquer all states")//TODO replace with some regions
    }
  }
  
}
