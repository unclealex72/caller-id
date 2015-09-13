package call

import com.typesafe.scalalogging.StrictLogging
import contact.ContactService
import number.NumberLocationService
import time.NowService

import scala.concurrent.{ExecutionContext, Future}
import scalaz._

/**
 * Created by alex on 04/09/15.
 */
class ReceivedCallFactoryImpl(
                               val contactService: ContactService,
                               val numberLocationService: NumberLocationService,
                               val nowService: NowService)(implicit val executionContext: ExecutionContext) extends ReceivedCallFactory with StrictLogging {

  override def create(number: Option[String]): Future[ReceivedCall] = {
    val now = nowService.now
    number match {
      case Some(number) =>
        numberLocationService.decompose(number) match {
          case Success(phoneNumber) =>
            contactService.contactNamesAndPhoneTypesForPhoneNumber(phoneNumber).map { contactNamesAndPhoneTypes =>
              ReceivedCall(now, Some(\/-(phoneNumber, contactNamesAndPhoneTypes)))
            }
          case Failure(errors) =>
            Future(ReceivedCall(now, Some(-\/(number))))
        }
      case None => Future(ReceivedCall(now, None))
    }
  }
}
