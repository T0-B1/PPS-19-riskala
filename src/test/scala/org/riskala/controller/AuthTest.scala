package org.riskala.controller

import java.io.FileNotFoundException
import java.util.Properties

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.junit.runner.RunWith
import org.scalatest.{Assertion, BeforeAndAfter}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class AuthTest extends AnyWordSpec with Matchers with BeforeAndAfter with ScalatestRouteTest {

  val properties: Properties = new Properties()

  before{
    val path = "/test.properties"
    val url = getClass.getResource(path)
    if (url != null)
      properties.load(Source.fromURL(url).bufferedReader())
    else
      throw new FileNotFoundException(s"Properties file at path $path cannot be loaded");
  }

  def login(username: String, password: String): String = {
    val json: String = LoginJsonSupport.LoginFormats.write(Login(username, password)).toString()
    Post("/login", HttpEntity(ContentTypes.`application/json`, json)) ~> RouteManager.allRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      responseAs[String]
    }
  }

  def register(username: String, password: String, email: String): Assertion = {
    val json: String = LoginJsonSupport.RegisterFormats.write(Register(username, password, email)).toString()
    Post("/register", HttpEntity(ContentTypes.`application/json`, json)) ~> RouteManager.allRoutes ~> check {
      response.status shouldEqual StatusCodes.BadRequest
    }
  }

  "A user" when {
    "registered" should {
      "be able to login" in {
        login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
      }
      "not be able to register again" in {
        register(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString, properties.get("testAccountPassword").toString)
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
