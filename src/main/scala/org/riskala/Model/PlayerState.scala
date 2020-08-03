package org.riskala.Model

/**
 * Represents the current state of a state during a game
 *
 * @param owner     the player who owns the state
 * @param troops    the number of troops to deploy
 * */
case class PlayerState(owner: Player, troops: Int)
