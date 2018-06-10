package push

import scala.concurrent.{ExecutionContext, Future}

trait PushEndpointDao {

  def upsert(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit]

  def all()(implicit ec: ExecutionContext): Future[Seq[PushSubscription]]
}
