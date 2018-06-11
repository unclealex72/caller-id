package push

import play.api.libs.json._

case class PushSubscription(endpoint: String, p256dh: String, auth: String)

private[push] case class Keys_(p256dh: String, auth: String)
private[push] case class Subscription_(endpoint: String, keys: Keys_)

object PushSubscription {
  implicit val pushEndpointFormat: OFormat[PushSubscription] = {
    implicit val keysFormat: OFormat[Keys_] = Json.format[Keys_]
    val subscriptionFormat: OFormat[Subscription_] = Json.format[Subscription_]
    new OFormat[PushSubscription] {
      override def writes(o: PushSubscription): JsObject = {
        subscriptionFormat.writes(Subscription_(o.endpoint, Keys_(o.p256dh, o.auth)))
      }

      override def reads(json: JsValue): JsResult[PushSubscription] = {
        subscriptionFormat.reads(json).map { subscription =>
          PushSubscription(subscription.endpoint, subscription.keys.p256dh, subscription.keys.auth)
        }
      }
    }
  }
}