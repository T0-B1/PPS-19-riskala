package org.riskala.model.eventsourcing

import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.{Geopolitics, Objectives, Player, PlayerState}
import org.riskala.utils.Utils

case class GameSnapshot(players: Seq[Player],
                        scenario: org.riskala.model.Map,
                        geopolitics: Geopolitics,
                        nowPlaying: Player,
                        deployableTroops: Int,
                        cards: Map[Player, Seq[Cards]],
                        objectives: Map[Player, Objective],
                        winner: Option[Player] = None)

object GameSnapshot {
  def newGame(players: Seq[Player],
              scenario: org.riskala.model.Map) : GameSnapshot = {
    val geopolitics: Geopolitics = Geopolitics(scenario.states.map(s=> PlayerState(s, Utils.randomSetElement[Player](players.toSet), 1)))
    val nowPlaying: Player = Utils.randomSetElement(players.toSet)
    val cards: Map[Player, Seq[Cards]] = players.map(p => (p, Seq.empty[Cards])).toMap
    val objectives: Map[Player, Objective] = players.map(p => (p, Objectives.generateRandomObjective(scenario, players.size))).toMap
    val deployable = statesOwned(nowPlaying, geopolitics)
    GameSnapshot(players, scenario, geopolitics, nowPlaying, deployable, cards, objectives)
  }

  def statesOwned(player: Player, geopolitics: Geopolitics) : Int =
    geopolitics.states.count(s => s.owner.equals(player))

}
