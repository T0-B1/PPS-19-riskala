package org.riskala.utils

import java.io.FileNotFoundException
import java.util.Properties

import scala.io.Source

object TestUtils {

  val PROPS_PATH = "/test.properties"

  def loadPropertiesFromResources(): Properties = {
    val properties: Properties = new Properties()
    val url = getClass.getResource(PROPS_PATH)
    if (url != null)
      properties.load(Source.fromURL(url).bufferedReader())
    else
      throw new FileNotFoundException(s"Properties file at path $PROPS_PATH cannot be loaded");
    properties
  }

}
