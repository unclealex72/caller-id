package push

import scala.concurrent.{ExecutionContext, Future}

trait BrowserPushService {

  def publicKey(): String

  def subscribe(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit]

  def notify(message: String)(implicit ec: ExecutionContext): Future[Unit]
}
