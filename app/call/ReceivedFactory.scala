package call

import scala.concurrent.Future

/**
 * Created by alex on 04/09/15.
 */
trait ReceivedFactory {

  def create(number: Option[String]): Future[Received]

  def ring: Future[Received]
}