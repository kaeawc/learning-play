import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.Logger
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global

class ApplicationSpec extends Specification {


  "GET /" should {

    "serve the landing page" in new WithApplication {

      val request = FakeRequest(GET, "/")

      val response = route(request).get

      status(response) must equalTo(OK)

      contentType(response) must beSome("text/html")

      contentAsString(response) mustEqual views.html.index().toString
    }
  }
}
