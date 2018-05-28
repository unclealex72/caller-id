package call

import modem.ModemResponse

import scala.concurrent.{ExecutionContext, Future}

trait CallService {

  def call(modemResponse: ModemResponse)(implicit ec: ExecutionContext): Future[Option[Call]]
}
