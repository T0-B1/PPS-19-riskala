package org.riskala.model

import argonaut.Argonaut.casecodec1

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}

/**
 * Structure of Cards
 * */
@JSExportTopLevel("Cards")
object Cards {

  sealed trait Card { val bonus: Int }

  /**
   * Infantry card data type
   * */
  @JSExport
  final case class Infantry(override val bonus:Int = 3) extends Card
  object Infantry{
    implicit def InfantryCodecJson =
      casecodec1(Infantry.apply,Infantry.unapply)("bonus")
  }

  /**
   * Cavalry card data type
   * */
  @JSExport
  final case class Cavalry(override val bonus:Int = 5 ) extends Card
  object Cavalry{
    implicit def CavalryCodecJson =
      casecodec1(Cavalry.apply,Cavalry.unapply)("bonus")
  }

  /**
   * Artillery card data type
   * */
  @JSExport
  final case class Artillery(override val bonus:Int = 7) extends Card
  object Artillery{
    implicit def ArtilleryCodecJson =
      casecodec1(Artillery.apply,Artillery.unapply)("bonus")
  }

  def generateCard(): Card = {
    val rng = scala.util.Random
    rng.nextInt(3) match {
      case 0 => Infantry()
      case 1 => Cavalry()
      case 2 => Artillery()
    }
  }
}

