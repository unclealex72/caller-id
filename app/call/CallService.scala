package call

import modem.ModemResponse

import scala.concurrent.{ExecutionContext, Future}

/**
  * A trait that turns a modem response in to a [[Call]], if possible.
  */
trait CallService {

  /**
    * Convert a response into an optional call.
    * @param modemResponse The [[ModemResponse]] to parse.
    * @return A future containing the call if the modem response is for a call.
    */
  def call(modemResponse: ModemResponse): Future[Option[Call]]
}
