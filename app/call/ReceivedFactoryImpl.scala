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
class ReceivedFactoryImpl(
                               val contactService: ContactService,
                               val numberLocationService: NumberLocationService,
                               val nowService: NowService)(implicit val executionContext: ExecutionContext) extends ReceivedFactory with StrictLogging {

  override def create(number: Option[String]): Future[CallReceived] = {
    val now = nowService.now
    number match {
      case Some(number) =>
        numberLocationService.decompose(number) match {
          case Success(phoneNumber) =>
            contactService.contactNamesAndPhoneTypesForPhoneNumber(phoneNumber).map { contactNamesAndPhoneTypes =>
              CallReceived(now, Some(\/-(phoneNumber, contactNamesAndPhoneTypes)))
            }
          case Failure(errors) =>
            Future(CallReceived(now, Some(-\/(number))))
        }
      case None => Future(CallReceived(now, None))
    }
  }

  override def ring: Future[Received] = Future(RingReceived)
}
