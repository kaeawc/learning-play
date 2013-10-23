package models

import java.util.Date

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

}