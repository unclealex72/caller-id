package modem

import com.typesafe.scalalogging.StrictLogging
import device.IoDevice

import scala.collection.immutable.Stream

/**
 * An implementation for AT modems.
 */
class AtzModem(val ioDevice: IoDevice) extends Modem with StrictLogging {

  val numberRegex = """NMBR = ([0-9]+)""".r

  def initialise() = {
    logger info "Initialising the modem."
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
      val decodedLine = bytes.filter(by => by >= 32 && by <= 127).map(_.toChar).mkString("")
      Unknown(decodedLine)
  }

  def close() = {
    logger info "Disconnecting from the modem."
    ioDevice.close()
  }
}
