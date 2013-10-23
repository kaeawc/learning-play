package models

import java.util.Date

import anorm._
import anorm.SqlParser._

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._

import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

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

  def parse(json:String) =
    Json.fromJson(Json.parse(json)).get

  val users =
    long("id") ~
    str("email") ~
    date("created") map {
      case   id~email~created =>
        User(id,email,created)
    }

  def getById(id:Long) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            u.id,
            u.email,
            u.created
          FROM user u
          WHERE u.id = {id};
        """
      ).on(
        'id -> id
      ).as(users.singleOpt)
    }
  }

  def findByEmail(email:String) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            u.id,
            u.email,
            u.created
          FROM user u
          WHERE u.email = {email};
        """
      ).on(
        'email -> email
      ).as(users *)
    }
  }

  def findRecent = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            u.id,
            u.email,
            u.created
          FROM user u;
        """
      ).as(users *)
    }
  }

  def create(email:String) = {

    val created = new Date()

    Future {
      DB.withConnection { implicit connection =>
        SQL(
          """
            INSERT INTO user (
              email,
              created
            ) VALUES (
              {email},
              {created}
            );
          """
        ).on(
          'email    -> email,
          'created  -> created
        ).executeInsert()
      }
    }.map {
      case id if id.isDefined =>
        Some(User(id.get,email,created))
      case _ =>
        None
    }
  }
}
