package contact

import number.{City, Country, NumberLocationService, PhoneNumber => NPhoneNumber}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable, ExecutionContext, Future}
import scalaz._

/**
 * Tests for ContactServiceImpl
 * Created by alex on 12/09/15.
 */
class ContactServiceImplSpec(implicit ev: ExecutionEnv) extends Specification with Mockito {

  class Context(val success: Boolean) extends Scope with NonEmptyListFunctions {
    val UK = Country("United Kingdom", "44", "uk", Some("0"), List.empty)
    val Basingstoke = City("Basingstoke", "1256")
    def pn(number: String): NPhoneNumber = NPhoneNumber(s"+$number", nels(UK), Some(Basingstoke), number)
    def spn(number: String): ValidationNel[String, NPhoneNumber] = Success(pn(number)).toValidationNel
    def fpn(number: String): ValidationNel[String, NPhoneNumber] = Failure(number).toValidationNel
    def pns(numbers: String*): Seq[Phone] = numbers.map { number => (number, Some("X")) }
    def await[R](body: Awaitable[R])(implicit ec: ExecutionContext): R = Await.result(body, 1.second)

    val numberLocationService = mock[NumberLocationService]
    val persistedContactService = mock[PersistedContactService]
    val contactService = new ContactServiceImpl(persistedContactService, numberLocationService)

    val phoneNumbers: Seq[Phone] = Map("123" -> Some("a"), "456" -> None, "789" -> Some("b"), "0ab" -> None).toSeq
    numberLocationService.decompose("123").returns(spn("123"))
    numberLocationService.decompose("456").returns(spn("456"))
    numberLocationService.decompose("789").returns(fpn("789"))
    numberLocationService.decompose("0ab").returns(fpn("0ab"))
    persistedContactService.addContact(any[String], any[ContactName], any[Seq[Phone]]).returns(Future(success))
  }

  "Adding to a contact who exists" should {
    "return a phone validation results class on the right hand side of an either" in new Context(true) {
      val result = await(contactService.addContact("brian", "freddie", phoneNumbers))
      result._1 must beTrue
      result._2 must beEqualTo(PhoneValidationResults(
        Map("+123" -> Some("a"), "+456" -> None).toSeq, Seq("789", "0ab")))
    }
  }

  "Adding to a contact who exists" should {
    "return a phone validation results class on the left hand side of an either" in new Context(false) {
      val result = await(contactService.addContact("brian", "freddie", phoneNumbers))
      result._1 must beFalse
      result._2 must beEqualTo(PhoneValidationResults(
        Map("+123" -> Some("a"), "+456" -> None).toSeq, Seq("789", "0ab")))
    }
  }
}
