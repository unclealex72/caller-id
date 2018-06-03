package call

import java.time.Instant

import call.PersistedCall._
import contact.Contact
import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class MongoDbPersistedCallDao(reactiveMongoApi: ReactiveMongoApi) extends
  MongoDbDao(reactiveMongoApi, "calls") with PersistedCallDao {

  override def insert(persistedCall: PersistedCall)(implicit ec: ExecutionContext): Future[Either[Seq[String], Unit]] = {
    for {
      calls <- collection()
      result <- calls.insert(persistedCall)
    } yield {
      result.toEither
    }
  }

  override def calls(max: Option[Int], since: Option[Instant], until: Option[Instant])(implicit ec: ExecutionContext): Future[Seq[PersistedCall]] = {
    val maxDocs: Int = max.getOrElse(Int.MaxValue)
    for {
      calls <- collection()
      cursor = calls.find(("when" ?>= since) && ("when" ?<= until)).sort("when".desc).cursor[PersistedCall]()
      persistedCalls <- cursor.collect[Seq](maxDocs, Cursor.FailOnError[Seq[PersistedCall]]())
    } yield {
      persistedCalls
    }
  }

  override def alterContacts(contacts: Seq[Contact])(implicit ec: ExecutionContext): Future[Either[Seq[String], Int]] = {
    def alter(calls: JSONCollection): Future[Either[Seq[String], Int]] = {
      contacts.toList match {
        case Nil =>
          Future.successful(Right(0))
        case firstContact :: otherContacts =>
          import calls.BatchCommands._
          import UpdateCommand._
          def toUpdateElement(contact: Contact): UpdateElement = {
            val selector: JsObject =
              "caller.type" === "unknown" && "caller.persistedPhoneNumber.normalisedNumber" === contact.normalisedPhoneNumber
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
