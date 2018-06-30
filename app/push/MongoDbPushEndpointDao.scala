package push

import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import reactivemongo.api.Cursor

import scala.concurrent.{ExecutionContext, Future}

/**
  * The MongoDB backed implementation of [[PushEndpointDao]]
  * @param reactiveMongoApi The underlying MongoDB API.
  * @param ec The execution context used to chain futures.
  */
class MongoDbPushEndpointDao(override val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext)
  extends MongoDbDao(reactiveMongoApi, "push-subscriptions") with PushEndpointDao {

  override def upsert(pushSubscription: PushSubscription): Future[Unit] = {
    for {
      coll <- collection()
      _ <- coll.update(pushSubscription, pushSubscription, upsert = true)
    } yield {
      {}
    }
  }

  override def all(): Future[Seq[PushSubscription]] = {
    for {
      coll <- collection()
      cursor = coll.find(Json.obj()).cursor[PushSubscription]()
      pushSubscriptions <- cursor.collect[Seq](-1, Cursor.FailOnError[Seq[PushSubscription]]())
    } yield {
      pushSubscriptions
    }
  }

  /**
    * Remove a stale push subscription.
    *
    * @param endpoint The endpoint to remove.
    * @return
    */
  override def remove(endpoint: String): Future[Unit] = {
    for {
      coll <- collection()
      _ <- coll.remove(Json.obj("endpoint" -> endpoint))
    } yield {
      {}
    }
  }
}


