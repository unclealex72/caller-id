package notify.sinks

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import call.{Call, CallView}
import squeezebox.Squeezebox

import scala.concurrent.{ExecutionContext, Future}

/**
  * A sink that displays calls on all Squeezeboxes.
  * @param squeezebox The [[Squeezebox]] used to send messages.
  * @param flowFactory A factory for flows to squeezeboxes.
  * @param ec
  */
class SqueezeboxSink(squeezebox: Squeezebox, flowFactory: () => Flow[ByteString, ByteString, _])(implicit ec: ExecutionContext) extends CallSink {
  override def consume(call: Call): Future[_] = {
    val callView: CallView = call.view
    val message: String = (callView.contact, callView.phoneNumber) match {
      case (Some(contact), _) => s"${contact.name} (${contact.phoneType})"
      case (_, Some(phoneNumber)) =>
        val address: String = (phoneNumber.city.toSeq :+ phoneNumber.countries.head).mkString(", ")
        s"${phoneNumber.formattedNumber} @ $address"
      case (_, _) => "Withheld"
    }
    squeezebox.display(flowFactory(), message)
  }

}
