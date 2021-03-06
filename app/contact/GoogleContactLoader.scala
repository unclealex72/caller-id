package contact

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.people.v1.PeopleService
import com.google.api.services.people.v1.model.{ListConnectionsResponse, Photo, PhoneNumber => GooglePhoneNumber}
import number.PhoneNumberFactory

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * The default implementation of [[ContactLoader]] that uses Google API calls.
  * @param numberLocationService
  */
class GoogleContactLoader(numberLocationService: PhoneNumberFactory)(implicit ec: ExecutionContext) extends ContactLoader {

  private val transport: HttpTransport = new NetHttpTransport()
  private val jacksonFactory: JsonFactory = new JacksonFactory()

  override def loadContacts(emailAddress: String, accessToken: String): Future[User] = Future {
    val googleCredential: GoogleCredential = new GoogleCredential.Builder().build().setAccessToken(accessToken)
    val peopleService: PeopleService =
      new PeopleService.Builder(transport, jacksonFactory, googleCredential).setApplicationName("Caller ID").build()
    val response: ListConnectionsResponse =
      peopleService.
        people().
        connections().
        list("people/me").
        setPersonFields("names,phoneNumbers,photos").
        execute()
    val contacts: Seq[Contact] = for {
      person <- response.getConnections.asScala
      name <- person.getNames.asScala
      googlePhoneNumber <- Option(person.getPhoneNumbers).map(_.asScala).getOrElse(Seq.empty[GooglePhoneNumber])
      canonicalForm <- Option(googlePhoneNumber.getCanonicalForm).toSeq
      phoneNumber <- numberLocationService.apply(canonicalForm).toOption.toSeq
    } yield {
      val maybePhoto: Option[Photo] = Option(person.getPhotos).map(_.asScala).flatMap(_.headOption)
      Contact(
        phoneNumber.normalisedNumber,
        name.getDisplayName,
        Option(googlePhoneNumber.getFormattedType).getOrElse("other"),
        maybePhoto.map(_.getUrl))
    }
    User(emailAddress, contacts.distinct)
  }
}
