package contact

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import slick.{InMemoryDatabaseProvider, TC, TP, TU}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
/**
 * Created by alex on 07/09/15.
 */
class SlickPersistedContactServiceSpec(implicit ee: ExecutionEnv) extends Specification {

  "Getting a list of all contacts with a number" should {
    "return all contacts with that number" in {
      val brian = TU("brian",
          TC("john", TP("1234", "Usual"), TP("5678", "Mobile")),
          TC("roger", TP("9999")))
      val freddie = TU("freddie",
        TC("john", TP("1234", "Usual")), TC("Ian"), TC("jane", TP("9999"), TP("1234")))
      val contactService = new SlickPersistedContactService(InMemoryDatabaseProvider(brian, freddie))
      contactService.contactNamesAndPhoneTypesForNormalisedNumber("1234")  must
        containTheSameElementsAs(Seq(("john", Some("Usual")), ("jane", None))).await
    }
  }

  "Getting a list of all known users' emails" should {
    "return all emails" in {
      val brian = TU("brian",
        TC("john", TP("1234", "Usual"), TP("5678", "Mobile")),
        TC("roger", TP("9999")))
      val freddie = TU("freddie",
        TC("john", TP("1234", "Usual")), TC("Ian"), TC("jane", TP("9999"), TP("1234")))
      val roger = TU("roger")
      val contactService = new SlickPersistedContactService(InMemoryDatabaseProvider(brian, freddie, roger))
      contactService.userEmails must containTheSameElementsAs(Seq("brian", "freddie", "roger")).await
    }
  }

  "Getting all stored users" should {
    "return all stored users" in {
      val brian = TU("brian",
        TC("john", TP("1234", "Usual"), TP("5678", "Mobile")),
        TC("roger", TP("9999")))
      val freddie = TU("freddie",
        TC("john", TP("1234", "Usual")), TC("Ian"), TC("jane", TP("9999"), TP("1234")))
      val roger = TU("roger")
      contacts(brian, freddie, roger)(_ => Future(true)) must beRight(containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None),
        ("freddie", "john", "1234", Some("Usual")),
        ("freddie", "jane", "9999", None),
        ("freddie", "jane", "1234", None))
      ))
    }
  }

  "Clearing a user's contacts" should {
    val brian = TU("brian",
      TC("john", TP("1234", "Usual"), TP("5678", "Mobile")),
      TC("roger", TP("9999")))
    val freddie = TU("freddie",
      TC("john", TP("1234", "Usual")), TC("Ian"), TC("jane", TP("9999"), TP("1234")))
    val roger = TU("roger")
    "clear that user's contacts but no-one else's" in {
      contacts(brian, freddie, roger)(_.clearContacts("freddie")) must beRight(containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None))
      ))
    }
    "do nothing if the user does not exist" in {
      contacts(brian, freddie, roger)(_.clearContacts("michael")) must beLeft(containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None),
        ("freddie", "john", "1234", Some("Usual")),
        ("freddie", "jane", "9999", None),
        ("freddie", "jane", "1234", None))
      ))
    }
  }

  "Adding a new contact" should {
    val brian = TU("brian",
      TC("john", TP("1234", "Usual"), TP("5678", "Mobile")),
      TC("roger", TP("9999")))
    val freddie = TU("freddie",
      TC("john", TP("1234", "Usual")), TC("Ian"), TC("jane", TP("9999"), TP("1234")))
    val roger = TU("roger")
    "add the contact and associate it to a user" in {
      contacts(brian, freddie, roger){
        _.addContact("freddie", "Mike", Seq(TP("9876"), TP("5432")))
      } must beRight(containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None),
        ("freddie", "john", "1234", Some("Usual")),
        ("freddie", "jane", "9999", None),
        ("freddie", "jane", "1234", None),
        ("freddie", "Mike", "5432", None),
        ("freddie", "Mike", "9876", None))
      ))
    }
    "do nothing if the user does not exist" in {
      contacts(brian, freddie, roger){
        _.addContact("dale", "Mike", Seq(TP("9876"), TP("5432")))
      } must beLeft(containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None),
        ("freddie", "john", "1234", Some("Usual")),
        ("freddie", "jane", "9999", None),
        ("freddie", "jane", "1234", None))
      ))
    }
  }

  type FlattenedContacts = Set[(String, ContactName, PhoneNumber, PhoneType)]

  def contacts(testUsers: TU*)(testCase: PersistedContactService => Future[Boolean]): Either[FlattenedContacts, FlattenedContacts] = {
    def flattened(allContacts: Map[String, Map[ContactName, Seq[Phone]]]) =
      for {
        email <- allContacts.keySet
        contact <- allContacts(email)
        phoneNumber <- contact._2
      } yield (email, contact._1, phoneNumber._1, phoneNumber._2)
    val contactService = new SlickPersistedContactService(InMemoryDatabaseProvider(testUsers: _*))
    val allContacts: Future[Either[FlattenedContacts, FlattenedContacts]] = testCase(contactService).flatMap { success =>
      contactService.allContacts.map(flattened).map { r =>
        if (success) Right(r) else Left(r)
      }
    }
    Await.result(allContacts, 1.second)
  }
}