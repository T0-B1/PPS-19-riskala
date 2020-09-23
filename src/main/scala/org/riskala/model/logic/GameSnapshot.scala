package org.riskala.model.logic

import org.riskala.model.Cards.Cards
import org.riskala.model.Objectives.Objective
import org.riskala.model.map.{Geopolitics, MapGeography, PlayerState}
import org.riskala.model.{Objectives, Player}
import org.riskala.utils.{MapLoader, Utils}

/**
 * An instance of a game in a given moment
 *
 * @param players Players
 * @param scenario Scenario
 * @param geopolitics State of the map wrt the owners of the state and the troops stationed in every state
 * @param nowPlaying Player whose turn it is
 * @param turn Number of the current turn
 * @param deployableTroops Max number of troops deployable by the current player
 * @param cards Set of cards in the hands of every player
 * @param objectives Objectives of every player
 * @param winner Optional winner of the game
 */
case class GameSnapshot(players: Seq[Player],
                        scenario: MapGeography,
                        geopolitics: Geopolitics,
                        nowPlaying: Player,
                        turn: Int,
                        deployableTroops: Int,
                        cards: Map[Player, Seq[Cards]],
                        objectives: Map[Player, Objective],
                        winner: Option[Player] = None)

object GameSnapshot {

  /**
   * Initializes a new game given a set of players and a scenario
   *
   * @param players Players
   * @param scenario A scenario
   * @return The initialized game
   */
  def newGame(players: Seq[Player],
              scenario: MapGeography) : GameSnapshot = {
    val geopolitics: Geopolitics = Geopolitics(scenario.states.map(s=> PlayerState(s, Utils.randomSetElement[Player](players.toSet), 1)))
    val nowPlaying: Player = Utils.randomSetElement(players.toSet)
    val cards: Map[Player, Seq[Cards]] = players.map(p => (p, Seq.empty[Cards])).toMap
    val objectives: Map[Player, Objective] = players.map(p => (p, Objectives.generateRandomObjective(scenario, players.size))).toMap
    val deployable = statesOwned(nowPlaying, geopolitics)
    GameSnapshot(players, scenario, geopolitics, nowPlaying, 1, deployable, cards, objectives)
  }

  /**
   * Initializes a new game given a set of players and the name of a scenario
   *
   * @param players Players
   * @param scenario A scenario
   * @return The initialized game
   */
  def newGame(players: Seq[Player],
              scenario: String) : GameSnapshot = {
    newGame(players, MapLoader.loadMap(scenario).get)
  }

  /**
   * Returns all the states owned by a player
   * @param player The owner
   * @param geopolitics The current status of the map
   * @return His states
   */
  def statesOwned(player: Player, geopolitics: Geopolitics) : Int =
    geopolitics.states.count(s => s.owner.equals(player))

}
