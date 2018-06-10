package contact

import scala.concurrent.Future

trait ContactDao {

  def upsertUser(user: User): Future[Either[Seq[String], Unit]]

  def findContactNameAndPhoneTypeForPhoneNumber(normalisedPhoneNumber: String): Future[Option[Contact]]
}
