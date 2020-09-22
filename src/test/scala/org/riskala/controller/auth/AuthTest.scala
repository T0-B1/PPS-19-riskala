package org.riskala.controller.auth

import java.util.Properties
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.junit.runner.RunWith
import org.riskala.controller.Server
import org.riskala.utils.Utils
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AuthTest extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val properties: Properties = Utils.loadPropertiesFromResources()

  "A user" when {
    "registered" should {
      "be able to login" in {
        AuthTest.login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
      }
      "not be able to register again" in {
        AuthTest.register(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString, properties.get("testAccountPassword").toString)
      }
    }
    "logged" should {
      "be able to verify his token" in {
        val token = AuthTest.login(properties.get("testAccountUsername").toString, properties.get("testAccountPassword").toString)
        AuthManager.checkToken(token) shouldBe true
      }
      "be able to retrieve his username from the token" in {
        val username = properties.get("testAccountUsername").toString
        val token = AuthTest.login(username, properties.get("testAccountPassword").toString)
        Some(username) shouldBe AuthManager.getUserName(token)
      }
    }
  }

}

object AuthTest extends AnyWordSpec with Matchers  with ScalatestRouteTest{

  def login(username: String, password: String): String = {
    val json: String = LoginJsonSupport.LoginFormats.write(LoginData(username, password)).toString()
    Post("/login", HttpEntity(ContentTypes.`application/json`, json)) ~> Server.routing ~> check {
      response.status shouldEqual StatusCodes.OK
      responseAs[String]
    }
  }

  def register(username: String, password: String, email: String): Assertion = {
    val json: String = LoginJsonSupport.RegisterFormats.write(RegistrationData(username, password, email)).toString()
    Post("/register", HttpEntity(ContentTypes.`application/json`, json)) ~> Server.routing ~> check {
      response.status shouldEqual StatusCodes.BadRequest
    }
  }
}
