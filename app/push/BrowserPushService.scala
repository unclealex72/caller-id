package push

import scala.concurrent.Future

/**
  * Send call notifications to browsers.
  */
trait BrowserPushService {

  /**
    * The VAPID public key.
    * @return
    */
  def publicKey(): String

  /**
    * Subscribe to a push subscription.
    * @param pushSubscription The push subscription to subscribe to.
    * @return
    */
  def subscribe(pushSubscription: PushSubscription): Future[Unit]

  /**
    * Send a message to all browsers.
    * @param message The message to send.
    * @return
    */
  def notify(message: String): Future[Unit]
}
