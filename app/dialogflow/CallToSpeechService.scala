package dialogflow

import call.Call

trait CallToSpeechService {

  def speak(call: Call): Option[String]
}
