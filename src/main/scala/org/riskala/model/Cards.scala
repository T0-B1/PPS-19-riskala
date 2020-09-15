package org.riskala.model

/**
 * Structure of Cards
 * */
object Cards {

  sealed trait Card { val bonus: Int }

  /**
   * Artillery card data type
   * @param bonus: the bonus associated to this card
   * */
  final case class Artillery(bonus:Int) extends Card

  /**
   * Cavalry card data type
   * @param bonus: the bonus associated to this card
   * */
  final case class Cavalry(bonus:Int) extends Card

  /**
   * Infantry card data type
   * @param bonus: the bonus associated to this card
   * */
  final case class Infantry(bonus:Int) extends Card

}

