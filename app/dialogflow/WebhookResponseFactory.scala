package dialogflow

import call.Call

trait WebhookResponseFactory {

  def generate(call: Call): String
}
