package org.riskala.utils

import org.riskala.model.MapGeography
import argonaut.Argonaut._
import better.files.Resource

object MapLoader {

  // TODO handle exceptions
  def loadMap(mapName: String) : Option[MapGeography] =  try{
      val jsonMap = Resource.getAsString(s"scenarios/$mapName.rkl")
      val deserializedMap: MapGeography = jsonMap.decodeOption[MapGeography].get
      Some(deserializedMap)
    } catch {
      case e: Exception => None
    }

}
