package contact

import scala.concurrent.{ExecutionContext, Future}

trait ContactLoader {

  def loadContacts(emailAddress: String, accessToken: String)(implicit ec: ExecutionContext): Future[User]
}
