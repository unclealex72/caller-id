package call

import java.time.Instant

import scala.concurrent.{ExecutionContext, Future}

trait PersistedCallDao {

  def insert(persistedCall: PersistedCall)(implicit ec: ExecutionContext): Future[Either[Seq[String], Unit]]

  def calls(maybeMax: Option[Int], maybeSince: Option[Instant], maybeUntil: Option[Instant])(implicit ec: ExecutionContext): Future[Seq[PersistedCall]]
}
