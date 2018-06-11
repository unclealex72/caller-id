package push

import java.security.Security

import call.{Call, CallView}
import com.typesafe.scalalogging.StrictLogging
import nl.martijndwars.webpush.{Notification, PushService}
import org.apache.http.HttpResponse
import org.bouncycastle.jce.provider.BouncyCastleProvider
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

class BrowserPushServiceImpl(pushConfiguration: BrowserPushConfiguration, pushEndpointDao: PushEndpointDao)
  extends BrowserPushService with StrictLogging {

  Security.addProvider(new BouncyCastleProvider)

  override val publicKey: String = pushConfiguration.publicKey

  val pushService = new PushService(publicKey, pushConfiguration.privateKey, pushConfiguration.domain)
  override def subscribe(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit] = {
    pushEndpointDao.upsert(pushSubscription)
  }

  override def notify(call: CallView)(implicit ec: ExecutionContext): Future[Unit] = {
    val message: String = Json.stringify(Json.toJson(call))
    for {
      pushSubscriptions <- pushEndpointDao.all()
      _ <- Future.sequence(pushSubscriptions.map { pushSubscription => notify(pushSubscription, message)})
    } yield {
      {}
    }
  }

  def notify(pushSubscription: PushSubscription, message: String)(implicit ec: ExecutionContext): Future[_] = Future {
    try {
      val notification =
        new Notification(pushSubscription.endpoint, pushSubscription.p256dh, pushSubscription.auth, message)
      val response: HttpResponse = pushService.send(notification)
      val statusCode: Int = response.getStatusLine.getStatusCode
      logger.info(s"Received response $statusCode: ${response.getStatusLine.getReasonPhrase}")
    } catch {
      case e: Exception =>
        logger.error(s"Could not send a notification to endpoint ${pushSubscription.endpoint}", e)
    }
  }
}