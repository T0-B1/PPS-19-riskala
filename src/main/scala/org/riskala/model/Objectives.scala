package org.riskala.model

import argonaut.Argonaut.casecodec2
import argonaut.CodecJson
import org.riskala.model.State.State

object Objectives {

  final case class Objective(states: Set[State] = Set.empty[State], info: String = "")
  implicit def ObjectiveCodecJson: CodecJson[Objective] =
    casecodec2(Objective.apply, Objective.unapply)("states", "info")

  private val minNumberOfPlayerForRnd: Int = 4

  /**
   * Utility to generate a random Objective based on map and number of player
   * @param map the map used for the random generated Objective
   * @param numberOfPlayer the numbre of player for the game where this Objective will be used
   * @return the random Objective based on params
   * */
  def generateRandomObjective(map: MapGeography, numberOfPlayer: Int): Objective = {
    numberOfPlayer match {
      case _ if numberOfPlayer < minNumberOfPlayerForRnd => Objective(map.states,"Conquer all states")
      case _ =>
        val range = math.ceil(map.regions.size.toDouble/3).toInt
        val regions = util.Random.shuffle(map.regions).slice(0, range)
        val states = regions.map(_.states).reduce(_++_)
        val info = regions.map(_.name).reduce(_+" "+_)
        Objective(states,s"Conquer states of regions $info")
    }
  }

}
