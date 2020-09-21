package org.riskala.utils

import java.io.FileNotFoundException
import java.util.Properties

import akka.http.scaladsl.model.StatusCodes
import akka.stream.scaladsl.Flow
import org.junit.runner.RunWith
import org.riskala.controller.routes.WebsocketRoute
import org.scalatest.Matchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

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