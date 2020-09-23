package org.riskala.model.logic

import org.riskala.model.Cards.Cards
import org.riskala.model.Player
import org.riskala.model.map.State.State

/**
 * An event happening in a game
 */
trait Event{

  /**
   * Returns a new instance of a game in which the event has happened
   *
   * @param game The game before the event
   * @return The game after the event
   */
  def happen(game: GameSnapshot): GameSnapshot
}

/**
 * The first event of a game
 *
 * @param initialSnapshot The starting point of a game
 */
final case class GameInitialized(initialSnapshot: GameSnapshot)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = game
}

/**
 * A battle event
 *
 * @param from The attacking state
 * @param to The target state
 * @param attacking The number of troops mobilized by the attacker
 * @param attackingPassed The number of attacking troops that invaded the target state
 * @param defendingCasualties The number of troops perished in the target state
 */
final case class Battle(from: State,
                       to: State,
                       attacking: Int,
                       attackingPassed: Int,
                       defendingCasualties: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    var geopolitics = game.geopolitics
    val attacker = geopolitics.getPlayerStateByName(from).get.owner
    geopolitics = geopolitics.modifyStateTroops(from, -attacking)
    if(attackingPassed > 0) {
      game.copy(geopolitics = geopolitics.setStateTroops(to, attackingPassed)
        .updateStateOwner(to, attacker))
    } else
      game.copy(geopolitics = geopolitics.modifyStateTroops(to, - defendingCasualties))
  }
}

/**
 * An event representing troops moving from on state of another
 * @param from The origin state
 * @param to The destination state
 * @param moved The number of troops moved
 */
final case class TroopsMoved(from: State,
                             to: State,
                             moved: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    game.copy(geopolitics = game.geopolitics
      .modifyStateTroops(from, -moved)
      .modifyStateTroops(to, +moved))
  }
}

/**
 * An event representing the deploying of troops
 * @param to The target state
 * @param troops The number of troops deployed
 */
final case class TroopsDeployed(to: State,
                        troops: Int)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    game.copy(geopolitics = game.geopolitics.modifyStateTroops(to, +troops),
      deployableTroops = game.deployableTroops - troops)
  }
}

/**
 * An event representing the drawing of a card
 *
 * @param player The player drawing the card
 * @param card The card drawn
 */
final case class CardDrawn(player: Player,
                      card: Cards)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    val playerCards = game.cards.getOrElse(player, Seq.empty[Cards]) :+ card
    game.copy(cards = game.cards + (player -> playerCards))
  }
}

/**
 * An event representing the receipt of a bonus
 *
 * @param player The player using the bonus
 * @param card The type of bonus
 */
final case class BonusRedeemed(player: Player,
                               card: Cards)
                  extends Event {

  private val numberOfCopies: Int = 3

  override def happen(game: GameSnapshot): GameSnapshot = {
    val toRemove = Seq.fill(numberOfCopies)(card)
    val playerCards = game.cards.getOrElse(player, Seq.empty[Cards]) diff toRemove
    val bonus = card.id
    game.copy(cards = game.cards + (player -> playerCards), deployableTroops = game.deployableTroops + bonus)
  }
}

/**
 * An event representing the end of a turn
 *
 * @param player The player who ends his turn
 */
final case class TurnEnded(player: Player)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    val currentPlayerIndex = game.players.indexOf(game.nowPlaying)
    val nextIndex = (currentPlayerIndex + 1) % game.players.size
    val nextPlayer = game.players(nextIndex)
    val nextTurn = game.turn + 1
    game.copy(nowPlaying = nextPlayer, turn = nextTurn, deployableTroops = game.geopolitics.count(p => p.owner.equals(nextPlayer)))
  }
}

/**
 * The last event of a game
 *
 * @param winner The winner of the game
 */
final case class GameEnded(winner: Player)
                  extends Event {
  override def happen(game: GameSnapshot): GameSnapshot = {
    game.copy(winner = Some(winner))
  }
}

