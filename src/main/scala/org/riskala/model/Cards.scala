package org.riskala.model

object Cards {

  sealed trait Card { val bonus: Int }

  final case class Artillery(bonus:Int) extends Card

  final case class Cavalry(bonus:Int) extends Card

  final case class Infantry(bonus:Int) extends Card

}

