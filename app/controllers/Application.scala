package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

import models.User

object Application extends Controller with FormBinding {

  def index = Action {
    Ok(views.html.index())
  }

  val emailForm = Form(
    "email" -> email
  )

  def sign = FormAction(emailForm) {
    email:String =>

    User.create(email) map {
      case Some(user:User) =>
        Accepted(Json.toJson(user))
      case _ =>
        internalError("Could not create User record.")
    }
  }
}
