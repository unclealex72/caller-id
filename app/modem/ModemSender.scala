package modem

trait ModemSender {

  def send(line: String): Unit
}
