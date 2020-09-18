package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.{MapGeography, Objectives, Player, PlayerState}
import org.riskala.utils.Utils

case class GameSnapshot(players: Seq[Player],
                        scenario: MapGeography,
                        geopolitics: Set[PlayerState],
                        nowPlaying: Player,
                        deployableTroops: Int,
                        cards: Map[Player, Set[Cards]],
                        objectives: Map[Player, Objective])

object GameSnapshot {
  def newGame(players: Seq[Player],
              scenario: MapGeography) : GameSnapshot = {
    val geopolitics: Set[PlayerState] = scenario.states.map(s=> PlayerState(s, Utils.randomSetElement[Player](players.toSet), 1))
    val nowPlaying: Player = Utils.randomSetElement(players.toSet)
    val cards: Map[Player, Set[Cards]] = players.map(p => (p, Set.empty[Cards])).toMap
    val objectives: Map[Player, Objective] = players.map(p => (p, Objectives.generateRandomObjective(scenario, players.size))).toMap
    GameSnapshot(players, scenario, geopolitics, nowPlaying, 0, cards, objectives)
  }
}
