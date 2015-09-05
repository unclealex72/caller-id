package modem

import device.IoDevice

import scala.collection.immutable.Stream

/**
 * An implementation for AT modems.
 */
class AtzModem(val ioDevice: IoDevice) extends Modem {

  val numberRegex = """NMBR = ([0-9]+)""".r

  def initialise() = {
    List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1") foreach { line =>
      ioDevice writeLine line
    }
  }

  def responses: Stream[ModemResponse] = ioDevice.readLines.filter(!_.isEmpty).map {
    case "OK" => Ok
    case "RING" => Ring
    case "NMBR = P" => Withheld
    case numberRegex(digits) => Number(digits)
    case line =>
      val bytes = line.getBytes.toList
      val decodedLine =
        if (bytes.forall(by => by >= 32 && by <= 127)) line else bytes.map(b => "%02x".format(b)).mkString(" ")
      Unknown(decodedLine)
  }

  def close() = {
    ioDevice.close()
  }
}
