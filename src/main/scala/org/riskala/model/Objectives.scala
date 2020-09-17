package org.riskala.model

import argonaut.Argonaut.casecodec2
import argonaut.CodecJson
import org.riskala.model.State.State

object Objectives {

  final case class Objective(states: Set[State], info: String)
  implicit def ObjectiveCodecJson: CodecJson[Objective] =
    casecodec2(Objective.apply, Objective.unapply)("", "")

  def generateRandomObjective(map: MapGeography, numberOfPlayer: Int): Objective = {
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
