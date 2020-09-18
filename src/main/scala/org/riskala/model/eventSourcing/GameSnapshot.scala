package org.riskala.model.eventSourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.{Objectives, Player, PlayerState}
import org.riskala.utils.Utils

case class GameSnapshot(players: Seq[Player],
                        scenario: org.riskala.model.Map,
                        geopolitics: Set[PlayerState],
                        nowPlaying: Player,
                        deployableTroops: Int,
                        cards: Map[Player, Seq[Cards]],
                        objectives: Map[Player, Objective])

object GameSnapshot {
  def newGame(players: Seq[Player],
              scenario: org.riskala.model.Map) : GameSnapshot = {
    val geopolitics: Set[PlayerState] = scenario.states.map(s=> PlayerState(s, Utils.randomSetElement[Player](players.toSet), 1))
    val nowPlaying: Player = Utils.randomSetElement(players.toSet)
    val cards: Map[Player, Seq[Cards]] = players.map(p => (p, Seq.empty[Cards])).toMap
    val objectives: Map[Player, Objective] = players.map(p => (p, Objectives.generateRandomObjective(scenario, players.size))).toMap
    GameSnapshot(players, scenario, geopolitics, nowPlaying, 0, cards, objectives)
  }
}
