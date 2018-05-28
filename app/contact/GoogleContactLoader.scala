package contact

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.people.v1.PeopleService
import com.google.api.services.people.v1.model.{PhoneNumber => GooglePhoneNumber}
import number.NumberLocationService

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class GoogleContactLoader(numberLocationService: NumberLocationService) extends ContactLoader {

  private val transport: HttpTransport = new NetHttpTransport()
  private val jacksonFactory: JsonFactory = new JacksonFactory()

  override def loadContacts(emailAddress: String, accessToken: String)(implicit ec: ExecutionContext): Future[User] = Future {
    val googleCredential = new GoogleCredential.Builder().build().setAccessToken(accessToken)
    val peopleService =
      new PeopleService.Builder(transport, jacksonFactory, googleCredential).build()
    val response =
      peopleService.
        people().
        connections().
        list("people/me").
        setPersonFields("names,phoneNumbers").
        execute()
    val contacts: Seq[Contact] = for {
      person <- response.getConnections.asScala
      name <- person.getNames.asScala
      googlePhoneNumber <- Option(person.getPhoneNumbers).map(_.asScala).getOrElse(Seq.empty[GooglePhoneNumber])
      canonicalForm <- Option(googlePhoneNumber.getCanonicalForm).toSeq
      phoneNumber <- numberLocationService.decompose(canonicalForm).toOption.toSeq
    } yield {
      Contact(phoneNumber.normalisedNumber, name.getDisplayName, Option(googlePhoneNumber.getFormattedType).getOrElse("other"))
    }
    User(emailAddress, contacts.distinct)
  }
}
