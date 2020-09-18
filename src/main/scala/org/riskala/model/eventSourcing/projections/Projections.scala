package org.riskala.model.eventSourcing.projections

import org.riskala.model.Cards.Cards
import org.riskala.model.Player
import org.riskala.model.eventSourcing.{BonusRedeemed, CardDrawn, Event}

trait Projection[State, Event] {
  def Init: State
  def Update: (State, Event) => State
  def Project: Seq[Event] => State = events => events.foldLeft(Init)(Update)
}

case class CardProjection() extends Projection[Map[Player, Seq[Cards]], Event] {

  override def Init: Map[Player, Seq[Cards]] = Map.empty

  override def Update: (Map[Player, Seq[Cards]], Event) => Map[Player, Seq[Cards]] = (cards, ev) => ev match {
    case drawn: CardDrawn => {
      val playerCards = cards.getOrElse(drawn.player, Seq.empty[Cards]) :+ drawn.card
      cards + (drawn.player -> playerCards)
    }
    case redeem: BonusRedeemed => {
      val toRemove = Seq.fill(3)(redeem.cardBonus)
      val playerCards = cards.getOrElse(redeem.player, Seq.empty[Cards]) diff toRemove
      cards + (redeem.player -> playerCards)
    }
    case _ => cards
  }
}
