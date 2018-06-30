package dialogflow

import call.Call

/**
  * A service that converts a [[Call]] into English.
  */
trait CallToSpeechService {

  /**
    * Speak a call.
    * @param call The call to speak.
    * @return The call in spoken english.
    */
  def speak(call: Call): String
}
