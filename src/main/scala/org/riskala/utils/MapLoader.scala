package org.riskala.utils

import org.riskala.model.{Map, MapImpl}
import argonaut.Argonaut._
import better.files.Resource

object MapLoader {

  def loadMap(mapName: String) : Option[Map] =  try{
      val jsonMap = Resource.getAsString(s"scenarios/$mapName.rkl")
      val deserializedMap: MapImpl = jsonMap.decodeOption[MapImpl].get
      Some(deserializedMap)
    } catch {
      case e: Exception => None
    }

}
