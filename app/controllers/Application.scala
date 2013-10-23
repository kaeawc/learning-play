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
      case Some(user:User) => Accepted(Json.toJson(user))
      case _               => internalError("Could not create User")
    }
  }

  def getById(id:Long) = Action.async {
    implicit request =>

    User.getById(id) map {
      case Some(user:User) => Ok(Json.toJson(user))
      case _               => internalError("Could not find User")
    }
  }

  def findByEmail(email:String) = Action.async {
    implicit request =>

    User.findByEmail(email) map {
      case user:List[User]
        if user.length > 0  => Ok(Json.toJson(user))
      case user:List[User]
        if user.length == 0 => NoContent
    }
  }

  def findRecent = Action.async {
    implicit request =>

    User.findRecent map {
      case user:List[User] => Ok(Json.toJson(user))
      case _               => internalError("Could not find User")
    }
  }
}
