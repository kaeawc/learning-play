package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

import models.User

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  val emailForm = Form(
    "email" -> text
  )

  def badForm[T](form:Form[T]) = Future {
    val errors = form.errors map {
      error => error.key -> JsString(error.message)
    }
    BadRequest(JsObject(errors))
  }

  def success(email:String) =
    User.create(email) map {
      case Some(user:User) =>
        Accepted(Json.toJson(user))
      case _ =>
        InternalServerError(
          Json.obj(
            "reason" -> "Could not create User record."
          )
        )
    }

  def sign = Action.async {
    implicit request =>
    emailForm.bindFromRequest match {
      case form:Form[String] if form.hasErrors => badForm[String](form)
      case form:Form[String] => success(form.get)
    }
  }

  implicit def kvToSeq(kv:(String,String)):JsValue = seqToJson(Seq(kv))
  implicit def seqToJson(seq:Seq[(String,String)]):JsValue = JsObject(seq.map { case(k,v) => (k,JsString(v))})

}