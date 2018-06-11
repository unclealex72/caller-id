package notify.sinks

import call.Call
import push.BrowserPushService

import scala.concurrent.{ExecutionContext, Future}

class PushNotificationSink(
                      browserPushService: BrowserPushService)(implicit ec: ExecutionContext) extends (Call => Future[_]) {

  override def apply(call: Call): Future[_] = {
    browserPushService.notify(call)
  }
}
