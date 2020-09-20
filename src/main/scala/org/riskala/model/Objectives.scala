package org.riskala.model

import org.riskala.model.State.State

object Objectives {

  final case class Objective(states: Set[State], info: String)

  def generateRandomObjective(map: Map, numberOfPlayer: Int): Objective = {
    numberOfPlayer match {
      case _ if numberOfPlayer < 4 => Objective(map.states,"Conquer all states")
      case _ =>
        val range = math.ceil(map.regions.size.toDouble/3).toInt
        val regions = util.Random.shuffle(map.regions).slice(0, range)
        val states = regions.map(_.states).reduce(_++_)
        val info = regions.map(_.name).reduce(_+" "+_)
        Objective(states,s"Conquer states of regions $info")
    }
  }

}
