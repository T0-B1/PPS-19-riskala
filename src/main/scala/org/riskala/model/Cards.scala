package org.riskala.model

import argonaut.Argonaut.casecodec1

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

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
  object Artillery{
    implicit def ArtilleryCodecJson =
      casecodec1(Artillery.apply,Artillery.unapply)("bonus")
  }

  /**
   * Cavalry card data type
   * @param bonus: the bonus associated to this card
   * */
  final case class Cavalry(bonus:Int) extends Card
  object Cavalry{
    implicit def CavalryCodecJson =
      casecodec1(Cavalry.apply,Cavalry.unapply)("bonus")
  }

  /**
   * Infantry card data type
   * @param bonus: the bonus associated to this card
   * */
  final case class Infantry(bonus:Int) extends Card
  object Infantry{
    implicit def InfantryCodecJson =
      casecodec1(Infantry.apply,Infantry.unapply)("bonus")
  }
}

