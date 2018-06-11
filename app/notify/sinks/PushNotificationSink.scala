package notify.sinks

import call.CallView
import play.api.libs.json.Json
import push.BrowserPushService

import scala.concurrent.{ExecutionContext, Future}

class PushNotificationSink(
                      browserPushService: BrowserPushService)(implicit ec: ExecutionContext) extends CallViewSink {


  override def consumeView(callView: CallView): Future[_] = {
    val message: String = Json.stringify(Json.toJson(callView))
    browserPushService.notify(message)
  }
}
