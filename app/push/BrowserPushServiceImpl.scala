package push

import java.security.Security

import com.typesafe.scalalogging.StrictLogging
import nl.martijndwars.webpush.{Notification, PushService}
import org.apache.http.StatusLine
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.bouncycastle.jce.provider.BouncyCastleProvider

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * The default implementation of [[BrowserPushService]]
  * @param pushConfiguration The VAPID keys and domain required to send notifications to browsers.
  * @param pushEndpointDao The DAO used to persist subscriptions.
  * @param ec The execution context used to chain futures.
  */
class BrowserPushServiceImpl(pushConfiguration: BrowserPushConfiguration, pushEndpointDao: PushEndpointDao)
                            (implicit ec: ExecutionContext)
  extends BrowserPushService with StrictLogging {

  Security.addProvider(new BouncyCastleProvider)

  override val publicKey: String = pushConfiguration.publicKey

  val pushService = new PushService(publicKey, pushConfiguration.privateKey, pushConfiguration.domain)
  override def subscribe(pushSubscription: PushSubscription): Future[Unit] = {
    pushEndpointDao.upsert(pushSubscription)
  }

  override def notify(message: String): Future[Unit] = {
    for {
      pushSubscriptions <- pushEndpointDao.all()
      _ <- Future.sequence(pushSubscriptions.map { pushSubscription => notify(pushSubscription, message)})
    } yield {
      {}
    }
  }

  def notify(pushSubscription: PushSubscription, message: String)(implicit ec: ExecutionContext): Future[_] = {
    val endpoint: String = pushSubscription.endpoint
    val notification =
      new Notification(endpoint, pushSubscription.p256dh, pushSubscription.auth, message)
    val post: HttpPost = pushService.preparePost(notification)
    val httpAsyncClient: CloseableHttpAsyncClient = HttpAsyncClients.createSystem
    val futureHttpAsyncClient = new FutureHttpAsyncClient(httpAsyncClient)
    httpAsyncClient.start()
    futureHttpAsyncClient.execute(post).andThen { case _ => httpAsyncClient.close() }.andThen {
      case Success(response) =>
        val statusLine: StatusLine = response.getStatusLine
        val statusCode: Int = statusLine.getStatusCode
        val phrase: String = statusLine.getReasonPhrase
        logger.info(s"""Received response $statusCode: "$phrase" from endpoint $endpoint""")
        if (statusCode == 410) {
          pushEndpointDao.remove(endpoint)
        }
      case Failure(e) =>
        logger.error(s"Could not send a notification to endpoint $endpoint", e)

    }
  }
}
