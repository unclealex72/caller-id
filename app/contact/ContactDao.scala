package contact

import scala.concurrent.Future

/**
  * A DAO for [[Contact]]s.
  */
trait ContactDao {

  /**
    * Add or insert a user and their contact's into the contact store.
    * @param user The user to insert or update if they already exist.
    * @return Eventually, either errors or [[Unit]]
    */
  def upsertUser(user: User): Future[Either[Seq[String], Unit]]

  /**
    * Find a contact by their normalised phone number.
    * @param normalisedPhoneNumber The number to look for.
    * @return Eventually, the contact if one was found.
    */
  def findContactNameAndPhoneTypeForPhoneNumber(normalisedPhoneNumber: String): Future[Option[Contact]]
}
