package call

import java.time.{Clock, Instant}

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import contact.{Contact, ContactDao}
import modem.ModemResponse
import number.PhoneNumberFactory

import scala.concurrent.{ExecutionContext, Future}

/**
  * The default implementation of [[CallService]]
  * @param clock
  * @param phoneNumberFactory
  * @param contactService
  * @param ec
  */
class CallServiceImpl(
                       clock: Clock,
                       phoneNumberFactory: PhoneNumberFactory,
                       contactService: ContactDao)
                     (implicit ec: ExecutionContext) extends CallService with StrictLogging {

  override def call(modemResponse: ModemResponse): Future[Option[Call]] = {
    val now: Instant = clock.instant()
    def call(caller: Caller): Option[Call] = Some(Call(now.toEpochMilli, caller))
    val none: Future[Option[Call]] = Future.successful(None)
    modemResponse match {
      case modem.Withheld => Future.successful(call(Withheld))
      case modem.Unknown(_) => none
      case modem.Number(number) => phoneNumberFactory(number) match {
        case Valid(phoneNumber) =>
          contactService.findContactNameAndPhoneTypeForPhoneNumber(phoneNumber.normalisedNumber).map {
            case Some(Contact(_, name, phoneType, avatarUrl)) => call(Known(name, phoneType, avatarUrl, phoneNumber))
            case None => call(Unknown(phoneNumber))
          }
        case Invalid(_) => none
      }
      case _ => none
    }
  }
}