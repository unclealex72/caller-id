package call

/**
 * Created by alex on 04/09/15.
 */
trait ReceivedCallFactory {

  def create(number: Option[String]): ReceivedCall
}