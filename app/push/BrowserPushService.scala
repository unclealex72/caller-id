package push

import call.{Call, CallView}

import scala.concurrent.{ExecutionContext, Future}

trait BrowserPushService {

  def publicKey(): String

  def subscribe(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit]

  def notify(call: CallView)(implicit ec: ExecutionContext): Future[Unit]
}
