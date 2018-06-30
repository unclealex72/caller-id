package modem

/**
  * A trait that allows a modem to be mocked by accepting messages that are then automatically passed on as genuine
  * modem commands.
  */
trait ModemSender {

  /**
    * Send a raw line to the modem.
    * @param line The line to send.
    */
  def send(line: String): Unit
}
