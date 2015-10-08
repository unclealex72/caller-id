package call

/**
 * A trait to turn received calls into displayable strings.
 * Created by alex on 12/09/15.
 */
trait ReceivedCallFormatter {

  def format(receivedCall: CallReceived): String
}

object ReceivedCallFormatter {

  implicit class ReceivedCallFormatterImplicits(receivedCall: CallReceived) {
    def format(implicit receivedCallFormatter: ReceivedCallFormatter) =
      receivedCallFormatter.format(receivedCall)
  }
}