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
 * The response when a witheld number calls.
 */
case object Witheld extends ModemResponse

/**
 * The response when a witheld number calls.
 */
case class Unknown(line: String) extends ModemResponse

/**
 * The response when a non-witheld number calls.
 */
case class Number(
                   /**
                    * The number sent via the modem.
                    */
                   number: String) extends ModemResponse
