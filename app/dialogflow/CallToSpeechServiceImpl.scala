package dialogflow

import java.time.format.DateTimeFormatter
import java.time.{OffsetDateTime, ZoneId}

import call.Call
import misc.NonEmptyListExtensions._

class CallToSpeechServiceImpl(dateTimeFormatter: DateTimeFormatter, zoneId: ZoneId) extends CallToSpeechService {
  override def speak(call: Call): Option[String] = {
    call.view.map { callView =>
      val when: String = dateTimeFormatter.format(OffsetDateTime.ofInstant(callView.when, zoneId))
      val caller: String = callView.contact match {
        case Some(contact) => s"${contact.name} called"
        case None =>
          callView.phoneNumber match {
            case Some(phoneNumber) =>
              val location: String = phoneNumber.city match {
                case Some(city) => s"$city, ${phoneNumber.countries.head}"
                case None => phoneNumber.countries.join(", ", " or ")
              }
              s"There was a call from ${phoneNumber.formattedNumber} in $location"
            case None => "There was a withheld call"
          }
      }
      s"$caller on $when"
    }
  }
}
