package org.riskala.model

import argonaut.Argonaut.casecodec1

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
 * Structure of Cards
 * */
object Cards {

  sealed trait Card { val bonus: Int }

  /**
   * Infantry card data type
   * */
  final case class Infantry() extends Card { val bonus:Int = 3 }
  object Infantry{
    implicit def InfantryCodecJson =
      casecodec1(Infantry.apply,Infantry.unapply)("bonus")
  }

  /**
   * Cavalry card data type
   * */
  final case class Cavalry() extends Card { val bonus:Int = 5 }
  object Cavalry{
    implicit def CavalryCodecJson =
      casecodec1(Cavalry.apply,Cavalry.unapply)("bonus")
  }

  /**
   * Artillery card data type
   * */
  final case class Artillery() extends Card { val bonus:Int = 7 }
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

