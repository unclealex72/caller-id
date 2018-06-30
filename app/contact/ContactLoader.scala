package contact

import scala.concurrent.Future

/**
  * A trait to load contacts from Google.
  */
trait ContactLoader {

  /**
    * Load contacts.
    * @param emailAddress The email address of the user.
    * @param accessToken The user's access token.
    * @return Eventually, all the user's contacts.
    */
  def loadContacts(emailAddress: String, accessToken: String): Future[User]
}
