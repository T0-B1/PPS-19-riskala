package org.riskala.model.game

import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.{Player, PlayerState}
import org.riskala.utils.Utils

case class GameSnapshot(players: Seq[Player],
                        scenario: org.riskala.model.Map,
                        geopolitics: Set[PlayerState],
                        nowPlaying: Player,
                        cards: Map[Player, Set[Cards]],
                        objectives: Map[Player, Objective])

object GameSnapshot {
  def newGame(players: Seq[Player],
              scenario: org.riskala.model.Map) : GameSnapshot = {
    val geopolitics: Set[PlayerState] = scenario.states.map(s=> PlayerState(Utils.randomSetElement[Player](players.toSet),1))

  }
}
