package play.api.mvc

import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

trait FormBinding {

  private def badForm[Tuple](form:Form[Tuple]) = Future {
    val errors = form.errors map {
      error => 
      error.key -> JsString(error.message)
    }
    
    BadRequest(JsObject(errors))
  }

  protected def internalError(reason:String) =
    InternalServerError(
      Json.obj(
        "reason" -> reason
      )
    )

  protected def FormAction[Tuple]
    (form    : Form[Tuple])
    (success : Tuple => Future[SimpleResult]) =
  Action.async {
    implicit request => BindForm(form)(success)
  }

  private def BindForm[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => Future[SimpleResult]
  )(implicit request:Request[AnyContent]) = {
    form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors =>
        badForm[Tuple](form)
      case form:Form[Tuple] =>
        success(form.get)
    }
  }
}
