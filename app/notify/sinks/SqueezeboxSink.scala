package notify.sinks

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import call.CallView
import squeezebox.Squeezebox

import scala.concurrent.{ExecutionContext, Future}

class SqueezeboxSink(squeezebox: Squeezebox, flowFactory: () => Flow[ByteString, ByteString, _])(implicit ec: ExecutionContext) extends CallViewSink {
  override def consumeView(callView: CallView): Future[_] = {
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
