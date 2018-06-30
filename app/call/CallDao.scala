package call

import java.time.Instant

import contact.Contact

import scala.concurrent.Future

/**
  * Persist and retrieve calls
  */
trait CallDao {

  /**
    * Insert a call
    * @param call The call to insert.
    * @return A future containing either a list of errors or [[Unit]]
    */
  def insert(call: Call): Future[Either[Seq[String], Unit]]

  /**
    * Update calls so that they reflect potentially new contact information.
    * @param contacts A list of contacts.
    * @return A future containing either a list of errors or [[Unit]]
    */
  def alterContacts(contacts: Seq[Contact]): Future[Either[Seq[String], Int]]

  /**
    * List all calls, possibly between instants, and possibly limit the number of calls.
    * @param max The maximum number of calls, if any.
    * @param since The time to search from, if any.
    * @param until The time to search to, if any.
    * @return A future containing a list of all the calls that match the search criteria.
    */
  def calls(
             max: Option[Int] = None,
             since: Option[Instant] = None,
             until: Option[Instant] = None): Future[Seq[Call]]
}
