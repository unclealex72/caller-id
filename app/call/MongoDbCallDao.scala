package call

import java.time.Instant

import call.Call._
import contact.Contact
import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import call.Call._

class MongoDbCallDao(reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) extends
  MongoDbDao(reactiveMongoApi, "calls") with CallDao {

  override def insert(call: Call): Future[Either[Seq[String], Unit]] = {
    for {
      calls <- collection()
      result <- calls.insert(call)
    } yield {
      result.toEither
    }
  }

  override def calls(max: Option[Int], since: Option[Instant], until: Option[Instant]): Future[Seq[Call]] = {
    val maxDocs: Int = max.getOrElse(Int.MaxValue)
    for {
      collection <- collection()
      cursor = collection.find(("when" ?>= since) && ("when" ?<= until)).sort("when".desc).cursor[Call]()
      calls <- cursor.collect[Seq](maxDocs, Cursor.FailOnError[Seq[Call]]())
    } yield {
      calls
    }
  }

  override def alterContacts(contacts: Seq[Contact]): Future[Either[Seq[String], Int]] = {
    def alter(calls: JSONCollection): Future[Either[Seq[String], Int]] = {
      contacts.toList match {
        case Nil =>
          Future.successful(Right(0))
        case firstContact :: otherContacts =>
          import calls.BatchCommands._
          import UpdateCommand._
          def toUpdateElement(contact: Contact): UpdateElement = {
            val selector: JsObject =
              "caller.type" === "unknown" && "caller.phoneNumber.normalisedNumber" === contact.normalisedPhoneNumber
            val update: JsObject = contact.avatarUrl.foldLeft(
              set("caller.name" -> JsString(contact.name),
                  "caller.type" -> JsString("known"),
                  "caller.phoneType" -> JsString(contact.phoneType))) { (update, avatarUrl) =>
              update && set("caller.avatarUrl" -> JsString(avatarUrl))
            }
            UpdateElement(selector, update, upsert = false, multi = true)
          }

          val update = Update(toUpdateElement(firstContact), otherContacts.map(c => toUpdateElement(c)): _*)
          calls.runCommand(update, ReadPreference.primary).map { result =>
            result.errmsg match {
              case Some(err) => Left(Seq(err))
              case _ => Right(result.nModified)
            }
          }
      }
    }
    collection().flatMap(alter)
  }
}
