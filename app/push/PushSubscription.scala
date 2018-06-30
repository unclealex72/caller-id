package push

import play.api.libs.json._

/**
  * A push subscription received from a browser that can be used to send notifications.
  * @param endpoint The endpoint that will receive notifications.
  * @param p256dh An Elliptic curve Diffie–Hellman public key on the P-256 curve.
  * @param auth An authentication secret.
  */
case class PushSubscription(endpoint: String, p256dh: String, auth: String, user: Option[String], userAgent: Option[String])

private[push] case class Keys_(p256dh: String, auth: String)
private[push] case class User_(user: Option[String], userAgent: Option[String])
private[push] case class Subscription_(endpoint: String, user: Option[User_], keys: Keys_)

/**
  * Convert a push subscription to and from JSON.
  */
object PushSubscription {
  implicit val pushEndpointFormat: OFormat[PushSubscription] = {
    implicit val keysFormat: OFormat[Keys_] = Json.format[Keys_]
    implicit val userFormat: OFormat[User_] = Json.format[User_]
    val subscriptionFormat: OFormat[Subscription_] = Json.format[Subscription_]
    new OFormat[PushSubscription] {
      override def writes(o: PushSubscription): JsObject = {
        val maybeUser: Option[User_] = (o.user, o.userAgent) match {
          case (None, None) => None
          case (mu, mua) => Some(User_(mu, mua))
        }
        subscriptionFormat.writes(
          Subscription_(o.endpoint, maybeUser, Keys_(o.p256dh, o.auth)))
      }

      override def reads(json: JsValue): JsResult[PushSubscription] = {
        subscriptionFormat.reads(json).map { subscription =>
          PushSubscription(
            subscription.endpoint,
            subscription.keys.p256dh,
            subscription.keys.auth,
            subscription.user.flatMap(_.user),
            subscription.user.flatMap(_.userAgent))
        }
      }
    }
  }
}