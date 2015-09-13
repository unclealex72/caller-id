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
 * Created by alex on 12/09/15.
 */
class ContactServiceImplSpec(implicit ev: ExecutionEnv) extends Specification with Mockito {

  trait Context extends Scope with NonEmptyListFunctions {
    val UK = Country("United Kingdom", "44", "uk", List.empty)
    val Basingstoke = City("Basingstoke", "1256")
    def pn(number: String): NPhoneNumber = NPhoneNumber(s"+$number", nels(UK), Some(Basingstoke), number)
    def spn(number: String): ValidationNel[String, NPhoneNumber] = Success(pn(number)).toValidationNel
    def fpn(number: String): ValidationNel[String, NPhoneNumber] = Failure(number).toValidationNel
    def pns(numbers: String*): Seq[Phone] = numbers.map { number => (number, Some("X")) }
    def await[R](body: Awaitable[R])(implicit ec: ExecutionContext): R = Await.result(body, 1.second)

    val numberLocationService = mock[NumberLocationService]
    val persistedContactService = mock[PersistedContactService]
    val contactService = new ContactServiceImpl(persistedContactService, numberLocationService)

    numberLocationService.decompose("123").returns(spn("123"))
    numberLocationService.decompose("456").returns(spn("456"))
    numberLocationService.decompose("789").returns(fpn("789"))
    numberLocationService.decompose("0ab").returns(fpn("0ab"))
    persistedContactService.updateTo(any[String], any[Map[ContactName, Seq[Phone]]]).returns(Future(true))

  }

  "Updating a contact with phone numbers that can be parsed" should {
    "Update all the contact information" in new Context {
      val result =
        await(contactService.update("brian", Map("freddie" -> pns("123", "456"), "roger" -> pns("456", "123")))).toEither
      result must beRight[Unit]
      there was one(persistedContactService).updateTo("brian", Map("freddie" -> pns("+123", "+456"), "roger" -> pns("+456", "+123")))
    }
  }

  "Updating a contact with phone numbers that can be parsed" should {
    "Update all the valid contact information and report the invalid contact information" in new Context {
      val result =
        await(contactService.update("brian", Map("freddie" -> pns("0ab", "456"), "roger" -> pns("789", "123")))).toEither
      result must beLeft { (errors: NonEmptyList[String]) =>
        errors.stream.toSeq must containTheSameElementsAs(Seq("0ab", "789"))
      }
      there was one(persistedContactService).updateTo("brian", Map("freddie" -> pns("+456"), "roger" -> pns("+123")))
    }
  }
}
