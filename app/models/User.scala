package models

import java.util.Date

import play.api.libs.json._

case class User(
  id      : Long,
  email   : String,
  created : Date
)

object User extends ((
  Long,
  String,
  Date
) => User) {

  implicit val r = Json.reads[User]
  implicit val w = Json.writes[User]

}