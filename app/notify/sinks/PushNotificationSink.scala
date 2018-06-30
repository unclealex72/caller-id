package notify.sinks

import call.Call
import play.api.libs.json.Json
import push.BrowserPushService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Send a call to each browser push service.
  * @param browserPushService The [[BrowserPushService]] that will send calls onwards.
  * @param ec
  */
class PushNotificationSink(
                      browserPushService: BrowserPushService)(implicit ec: ExecutionContext) extends CallSink {


  override def consume(call: Call): Future[_] = {
    val message: String = Json.stringify(Json.toJson(call.view))
    browserPushService.notify(message)
  }
}
