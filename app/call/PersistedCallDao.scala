package call

import java.time.Instant

import contact.Contact

import scala.concurrent.{ExecutionContext, Future}

trait PersistedCallDao {

  def insert(persistedCall: PersistedCall)(implicit ec: ExecutionContext): Future[Either[Seq[String], Unit]]

  def alterContacts(contacts: Seq[Contact])
                   (implicit ec: ExecutionContext): Future[Either[Seq[String], Int]]

  def calls(
             max: Option[Int] = None,
             since: Option[Instant] = None,
             until: Option[Instant] = None)(implicit ec: ExecutionContext): Future[Seq[PersistedCall]]
}
