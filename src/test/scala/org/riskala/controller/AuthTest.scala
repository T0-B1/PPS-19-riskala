package org.riskala.controller

import java.io.FileNotFoundException
import java.util.Properties

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, Matchers}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class AuthTest extends AnyWordSpec with BeforeAndAfter with ScalatestRouteTest {

  val properties: Properties = new Properties()

  before{
    val path = "/test.properties"
    val url = getClass.getResource(path)
    if (url != null)
      properties.load(Source.fromURL(url).bufferedReader())
    else
      throw new FileNotFoundException(s"Properties file at path $path cannot be loaded");
  }

  properties.get("testAccountUsername")
  properties.get("testAccountPassword")
  properties.get("testAccountEmail")

  "A user" when {
    "registered" should {
      "be able to login" in {

      }
      "not be able to register again" in {

      }
    }
    "logged" should {
      "be able to verify his token" in {

      }
      "be able to retrieve his username from the token" in {

      }
    }
  }

}
