package push

import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._

import scala.concurrent.{ExecutionContext, Future}

class MongoDbPushEndpointDao(override val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext)
  extends MongoDbDao(reactiveMongoApi, "push-subscriptions") with PushEndpointDao {

  override def upsert(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      coll <- collection()
      _ <- coll.update(pushSubscription, pushSubscription, upsert = true)
    } yield {
      {}
    }
  }

  override def all()(implicit ec: ExecutionContext): Future[Seq[PushSubscription]] = {
    for {
      coll <- collection()
      cursor = coll.find(Json.obj()).cursor[PushSubscription]()
      pushSubscriptions <- cursor.collect[Seq]()
    } yield {
      pushSubscriptions
    }
  }
}


