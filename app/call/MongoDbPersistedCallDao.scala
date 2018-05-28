package call

import java.time.Instant

import call.PersistedCall._
import persistence.MongoDbDao
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.play.json._

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

  override def calls(maybeMax: Option[Int], maybeSince: Option[Instant], maybeUntil: Option[Instant])(implicit ec: ExecutionContext): Future[Seq[PersistedCall]] = {
    val maxDocs = maybeMax.getOrElse(Int.MaxValue)
    for {
      calls <- collection()
      cursor = calls.find(("when" ?>= maybeSince) && ("when" ?<= maybeUntil)).sort("when".asc).cursor[PersistedCall]()
      persistedCalls <- cursor.collect[Seq](maxDocs, Cursor.FailOnError[Seq[PersistedCall]]())
    } yield {
      persistedCalls
    }
  }
}
