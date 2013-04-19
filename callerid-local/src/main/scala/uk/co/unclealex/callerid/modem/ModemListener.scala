package uk.co.unclealex.callerid.modem

import uk.co.unclealex.callerid.call.CallController
import uk.co.unclealex.callerid.device.Device
import uk.co.unclealex.process.packages.PackagesRequired
import scala.collection.immutable.Stream

@PackagesRequired(Array("ser2net"))
class ModemListener(modemDevice: Device, callController: CallController) extends Runnable {

  /**
   * Initialise the modem and then listen for calls.
   */
  override def run() {
    initialiseModem;
    listenForCalls;
  }

  /**
   * Send any required initilisation command strings to the modem.
   */
  def initialiseModem: Unit = {
    List("ATZ", "AT+FCLASS=1.0", "AT+VCID=1").foreach(
      modemDevice.writeLine(_))
  }

  /**
   * Listen for any calls and then notify the {@link #callController}.
   */
  def listenForCalls: Unit = {
    Stream.continually(modemDevice readLine).takeWhile(_.isDefined).foreach(
      _.foreach(callController onCall _))
  }

}