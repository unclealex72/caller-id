package modem

/**
 * An abstract of all the valid modem responses.
 */
sealed trait ModemResponse

/**
 * The OK response to a sent command
 */
case object Ok extends ModemResponse

/**
 * The RING command when the phone rings.
 */
case object Ring extends ModemResponse

/**
 * The response when a withheld number calls.
 */
case object Withheld extends ModemResponse

/**
 * The response when a line that cannot be understood is returned from the modem.
 */
case class Unknown(line: String) extends ModemResponse

/**
 * The response when a non-withheld number calls.
 */
case class Number(
                   /**
                    * The number sent via the modem.
                    */
                   number: String) extends ModemResponse
