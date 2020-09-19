package org.riskala.model.eventSourcing.projections

import org.riskala.model.Cards.Cards
import org.riskala.model.eventSourcing.{BonusRedeemed, CardDrawn, Event, GameSnapshot}

class CardsProjection extends Projection[GameSnapshot, Event] {

  override def Update: (GameSnapshot, Event) => GameSnapshot = (game, ev) => ev match {
    case drawn: CardDrawn => {
      val playerCards = game.cards.getOrElse(drawn.player, Seq.empty[Cards]) :+ drawn.card
      val cards = game.cards + (drawn.player -> playerCards)
      game.copy(cards = cards)
    }
    case redeem: BonusRedeemed => {
      val toRemove = Seq.fill(3)(redeem.cardBonus)
      val playerCards = game.cards.getOrElse(redeem.player, Seq.empty[Cards]) diff toRemove
      val cards = game.cards + (redeem.player -> playerCards)
      game.copy(cards = cards)
    }
    case _ => game
  }
}
