package contact

import persistence.MongoDbDaoSpec
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

class MongoDbContactDaoSpec extends MongoDbDaoSpec[MongoDbContactDao]("contacts") {

  "The initialised database" should {
    "contain 4 contacts" in { f =>
      val eventualElementCount = for {
        db <- f.db
        col = db.collection[BSONCollection](f.dao.collectionName)
        cnt <- col.find(BSONDocument()).cursor[BSONDocument]().fold(0)((acc, _) => acc + 1)
      } yield {
        cnt
      }
      eventualElementCount.map { elementCount =>
        elementCount should ===(4)
      }
    }
  }

  "Searching for a known number" should {
    "return the contact who owns it" in { f =>
      f.dao.findContactNameAndPhoneTypeForPhoneNumber("01818118181").map {
        case Some(contact) => contact.name should ===("The BBC")
        case _ => fail("Could not find the BBC")
      }
    }
  }

  "Searching for an unknown number" should {
    "return nothing" in { f =>
      f.dao.findContactNameAndPhoneTypeForPhoneNumber("02828228282").map { maybeContact =>
        maybeContact should ===(None)
      }
    }
  }

  "Upserting a user" should {
    "Upsert the user but leave all other users alone" in { f =>
      val user = User("freddie@queen", Seq(Contact("01256767879", "Basingstoke Taxis", "main", Some("http://taxi"))))
      for {
        _ <- f.dao.upsertUser(user)
        db <- f.db
        col = db.collection[BSONCollection](f.dao.collectionName)
        contactsWithId <- col.find(BSONDocument()).cursor[BSONDocument]().fold(Seq.empty[BSONDocument])(_ :+ _)
      } yield {
        val contacts = contactsWithId.map(doc => doc.--("_id"))
        contacts should contain theSameElementsAs Seq(
          contact("brian@queen", "01818118181", "The BBC", "main", Some("http://bbc")),
          contact("brian@queen", "01611234567", "Jodrell Bank", "main", None),
          contact("freddie@queen", "01256767879", "Basingstoke Taxis", "main", Some("http://taxi"))
        )
      }
    }
  }

  def contact(emailAddress: String, normalisedPhoneNumber: String, name: String, phoneType: PhoneType, maybeAvatarUrl: Option[String]): BSONDocument = {
    BSONDocument(Seq(
      "userEmailAddress" -> BSONString(emailAddress),
      "normalisedPhoneNumber" -> BSONString(normalisedPhoneNumber),
      "name" -> BSONString(name),
      "phoneType" -> BSONString(phoneType)) ++ maybeAvatarUrl.map(url => "avatarUrl" -> BSONString(url)))
  }

  override def createDao(reactiveMongoApi: ReactiveMongoApi): MongoDbContactDao = {
    new MongoDbContactDao(reactiveMongoApi)
  }

  override def initialData(): Seq[BSONDocument] = {
    Seq(
      contact("freddie@queen", "01818118181", "The BBC", "main", None),
      contact("freddie@queen", "07001234567", "Delilah", "mobile", None),
      contact("brian@queen", "01818118181", "The BBC", "main", Some("http://bbc")),
      contact("brian@queen", "01611234567", "Jodrell Bank", "main", None)
    )
  }
}