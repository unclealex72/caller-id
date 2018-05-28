package contact

import scala.concurrent.{ExecutionContext, Future}

trait ContactDao {

  def upsertUser(user: User)(implicit ec: ExecutionContext): Future[Either[Seq[String], Unit]]

  def findContactNameAndPhoneTypeForPhoneNumber(normalisedPhoneNumber: String)(implicit ec: ExecutionContext): Future[Option[Contact]]
}
