package call

import java.time.Instant

import contact.Contact

import scala.concurrent.Future

trait CallDao {

  def insert(call: Call): Future[Either[Seq[String], Unit]]

  def alterContacts(contacts: Seq[Contact]): Future[Either[Seq[String], Int]]

  def calls(
             max: Option[Int] = None,
             since: Option[Instant] = None,
             until: Option[Instant] = None): Future[Seq[Call]]
}
