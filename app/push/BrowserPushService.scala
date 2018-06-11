package push

import call.Call

import scala.concurrent.{ExecutionContext, Future}

trait BrowserPushService {

  def publicKey(): String

  def subscribe(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit]

  def notify(call: Call)(implicit ec: ExecutionContext): Future[Unit]
}
