package call

import com.typesafe.scalalogging.StrictLogging
import number.NumberLocationService
import time.NowService

/**
 * Created by alex on 04/09/15.
 */
class ReceivedCallFactoryImpl(
                               val numberLocationService: NumberLocationService,
                               val nowService: NowService) extends ReceivedCallFactory with StrictLogging {

  override def create(number: Option[String]): ReceivedCall = {
    val phoneNumber = number.map { number =>
      numberLocationService.decompose(number).disjunction.leftMap { errors =>
        errors.foreach(error => logger.warn(error))
        number
      }
    }
    ReceivedCall(nowService.now, phoneNumber, None)
  }
}
