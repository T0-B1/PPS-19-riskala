package org.riskala.utils

import argonaut.Argonaut._
import org.riskala.model.map.MapGeography
import scala.io.Source

object MapLoader {

  def loadMap(mapName: String) : Option[MapGeography] =  try{
      val jsonMap = Source.fromResource(s"scenarios/$mapName.rkl").mkString
      val deserializedMap: MapGeography = jsonMap.decodeOption[MapGeography].get
      Some(deserializedMap)
    } catch {
      case e: Exception => None
    }

}
