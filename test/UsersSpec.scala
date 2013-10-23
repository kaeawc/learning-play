import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.Logger
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global

class UserSpec extends Specification {

  import models.User
  import models.User._

  "getById" should {

    "return an User if one exists" in new WithApplication {

      val (email) = ("someone@example.com")

      create(email) map {
        case Some(user:User) => {

          val request = FakeRequest(GET, "/user/" + user.id)

          val response = route(request).get

          status(response) must equalTo(OK)
          contentType(response) must beSome("application/json")

          parse(contentAsString(response)) match {
            case User(id,email,created) => user.email mustEqual email
            case _ => failure("Could not parse the response as an User object")
          }
        }
        case _ => failure("Failed to create user for test.")
      }
    }

    "return NotFound if the user doesn't exist" in new WithApplication {

      val request = FakeRequest(GET, "/user/-5")

      val response = route(request).get

      status(response) must equalTo(404)
      contentType(response) must beNone
    }
  }

  "findByEmail" should {

    "return an User if one exists" in new WithApplication {

      val (email) = ("someone@example.com")

      create(email) map {
        case Some(user:User) => {
          val request = FakeRequest(GET, "/user/with/email/someone@example.com")

          val response = route(request).get

          status(response) must equalTo(OK)
          contentType(response) must beSome.which(_ == "application/json")

          parse(contentAsString(response)) match {
            case User(id,email,created) => user.email mustEqual email
            case _ => failure("Could not parse the response as an User object")
          }
        }
        case _ => failure("Failed to create user for test.")
      }
    }

    "return NotFound if user doesn't exist" in new WithApplication {

      val request = FakeRequest(GET, "/user/with/email/something@nowhere.com")

      val response = route(request).get

      status(response) must equalTo(404)
      contentType(response) must beNone
    }
  }


  "create" should {

    "return Accepted if creation was performed correctly" in new WithApplication {

      val header = FakeRequest(POST, "/user")

      val data = Json.obj("email" -> "someone@example.com")

      val response = route(header,data).get

      status(response) must equalTo(201)
      contentType(response) must beSome("application/json")

      parse(contentAsString(response)) match {
        case user:User => {
          user.email mustEqual "someone@example.com"
        }
        case _ => failure("Could not parse the response as an User object")
      }
    }

    "return BadRequest if not all information was sent." in new WithApplication {

      val request = FakeRequest(POST, "/user")

      val response = route(request).get

      status(response) must equalTo(400)
      contentType(response) must beSome("application/json")
      contentAsString(response) mustEqual(Json.obj("email" -> "error.required").toString)
    }
  }
}
