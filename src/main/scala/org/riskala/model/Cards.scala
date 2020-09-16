package org.riskala.model

import argonaut.Argonaut._
import argonaut.{CodecJson, DecodeResult}

/**
 * Structure of Cards
 * */
object Cards extends Enumeration {

  private val rng = scala.util.Random

  type Cards = Value
  val Infantry: Cards = Value(3, "Infantry")
  val Cavalry: Cards = Value(5, "Cavalry")
  val Artillery: Cards = Value(7, "Artillery")

  implicit def CardEnumCodecJson: CodecJson[Cards] = CodecJson({
    e: Cards => e.toString.asJson
  }, c => c.focus.string match {
    case Some(n:String) if Cards.values.exists(_.toString==n) => DecodeResult.ok(Cards.withName(n))
    case _ => DecodeResult.fail("Could not decode CardEnum",c.history)
  })

  def generateCard(): Cards = {
    Cards.values.toSeq(rng.nextInt(Cards.values.size))
  }

}

