package org.riskala.utils

import org.riskala.model.MapGeography
import argonaut.Argonaut._

import scala.io.Source

object MapLoader {

  // TODO handle exceptions
  def loadMap(mapName: String) : Option[MapGeography] =  try{
      val jsonMap = Source.fromResource(s"scenarios/$mapName.rkl").mkString
      val deserializedMap: MapGeography = jsonMap.decodeOption[MapGeography].get
      Some(deserializedMap)
    } catch {
      case e: Exception => None
    }

}
