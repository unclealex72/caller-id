package modem

/**
 * A trait describing the output of a modem.
 * Created by alex on 31/08/15.
 */
trait Modem extends AutoCloseable {

  /**
   * Send any required initialisation command strings to the modem.
   */
  def initialise(): Unit

  /**
   *
   * @return A stream of modem responses as they are received from the modem.
   */
  def responses: Stream[ModemResponse]
}
