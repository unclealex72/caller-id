package contact

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import slick.{TP, TU, TC, InMemoryDatabaseProvider}

import scala.concurrent.Future
import scalaz._
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
      val contactService = new SlickPersistedContactService(InMemoryDatabaseProvider(brian, freddie, roger))
      contactService.allContacts.map(flattened) must containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None),
        ("freddie", "john", "1234", Some("Usual")),
        ("freddie", "jane", "9999", None),
        ("freddie", "jane", "1234", None)
      )).await
    }
  }

  "Refreshing a user's contacts" should {
    "refresh that user's contacts but no-one else's" in {
      val brian = TU("brian",
        TC("john", TP("1234", "Usual"), TP("5678", "Mobile")),
        TC("roger", TP("9999")))
      val freddie = TU("freddie",
        TC("john", TP("1234", "Usual")), TC("Ian"), TC("jane", TP("9999"), TP("1234")))
      val roger = TU("roger")
      val contactService = new SlickPersistedContactService(InMemoryDatabaseProvider(brian, freddie, roger))
      val allContacts = contactService.updateTo(
        "freddie",
        Map("brian" -> Seq(("1234", None), ("4567", Some("Old"))))).flatMap { success =>
        if (success) contactService.allContacts.map(\/-(_)) else Future(-\/(success)) }
      allContacts.map { either => either.map(flattened).toEither }  must beRight(containTheSameElementsAs(Seq(
        ("brian", "john", "1234", Some("Usual")),
        ("brian", "john", "5678", Some("Mobile")),
        ("brian", "roger", "9999", None),
        ("freddie", "brian", "1234", None),
        ("freddie", "brian", "4567", Some("Old")))
      )).await
    }
  }

  def flattened(allContacts: Map[String, Map[String, Seq[(String, Option[String])]]]) =
    for {
      email <- allContacts.keySet
      contact <- allContacts(email)
      phoneNumber <- contact._2
    } yield (email, contact._1, phoneNumber._1, phoneNumber._2)

}
