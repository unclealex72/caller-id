package notify.sinks

import call.Call
import cats.data._
import cats.implicits._
import play.api.libs.json.Json
import push.BrowserPushService

import scala.concurrent.{ExecutionContext, Future}

class PushNotificationSink(
                      browserPushService: BrowserPushService)(implicit ec: ExecutionContext) extends CallSink {

  override def consume(call: Call): Future[_] = {
    for {
      view <- OptionT(Future.successful(call.view))
      _ <- OptionT(Some(browserPushService.notify(message)))
    }
    call.view.foldLeft(Future.successful()) { (acc, view) =>
      val message: String = Json.stringify(Json.toJson(view))
      acc.flatMap(_ => browserPushService.notify(message))
    }
  }
}
