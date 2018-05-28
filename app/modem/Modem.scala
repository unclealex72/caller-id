package modem

import akka.stream.scaladsl._

/**
 * A trait describing the output of a modem.
 * Created by alex on 31/08/15.
 */
trait Modem {

  /**
   *
   * @return A source of modem responses as they are received from the modem.
   */
  def responses(): Source[ModemResponse, Disconnect]

  /**
    * A class that can be used to disconnect fromm the modem.
    */
  trait Disconnect {

    /**
      * Disconnect from a modem.
      */
    def disconnect()
  }
}
