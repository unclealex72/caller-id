package call

import java.time.Clock

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import contact.{Contact, ContactDao}
import javax.inject.Inject
import modem.ModemResponse
import number.NumberLocationService

import scala.concurrent.{ExecutionContext, Future}

class CallServiceImpl @Inject() (clock: Clock, numberLocationService: NumberLocationService, contactService: ContactDao) extends CallService with StrictLogging {

  override def call(modemResponse: ModemResponse)(implicit ec: ExecutionContext): Future[Option[Call]] = {
    val now = clock.instant()
    def call(caller: Caller): Option[Call] = Some(Call(now, caller))
    def undefinable(line: String): Future[Option[Call]] = Future.successful(call(Undefinable(line)))
    modemResponse match {
      case modem.Withheld => Future.successful(call(Withheld))
      case modem.Unknown(line) => undefinable(line)
      case modem.Number(number) => numberLocationService.decompose(number) match {
        case Valid(phoneNumber) =>
          contactService.findContactNameAndPhoneTypeForPhoneNumber(phoneNumber.normalisedNumber).map {
            case Some(Contact(_, name, phoneType, avatarUrl)) => call(Known(name, phoneType, avatarUrl, phoneNumber))
            case None => call(Unknown(phoneNumber))
          }
        case Invalid(_) => undefinable(number)
      }
      case _ => Future.successful(None)
    }
  }
}