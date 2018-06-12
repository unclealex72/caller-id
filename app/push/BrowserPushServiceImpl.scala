package push

import java.security.Security

import com.typesafe.scalalogging.StrictLogging
import nl.martijndwars.webpush.{Notification, PushService}
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.bouncycastle.jce.provider.BouncyCastleProvider

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BrowserPushServiceImpl(pushConfiguration: BrowserPushConfiguration, pushEndpointDao: PushEndpointDao)
  extends BrowserPushService with StrictLogging {

  Security.addProvider(new BouncyCastleProvider)

  override val publicKey: String = pushConfiguration.publicKey

  val pushService = new PushService(publicKey, pushConfiguration.privateKey, pushConfiguration.domain)
  override def subscribe(pushSubscription: PushSubscription)(implicit ec: ExecutionContext): Future[Unit] = {
    pushEndpointDao.upsert(pushSubscription)
  }

  override def notify(message: String)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      pushSubscriptions <- pushEndpointDao.all()
      _ <- Future.sequence(pushSubscriptions.map { pushSubscription => notify(pushSubscription, message)})
    } yield {
      {}
    }
  }

  def notify(pushSubscription: PushSubscription, message: String)(implicit ec: ExecutionContext): Future[_] = {
    val notification =
      new Notification(pushSubscription.endpoint, pushSubscription.p256dh, pushSubscription.auth, message)
    val post: HttpPost = pushService.preparePost(notification)
    val httpAsyncClient: CloseableHttpAsyncClient = HttpAsyncClients.createSystem
    val futureHttpAsyncClient = new FutureHttpAsyncClient(httpAsyncClient)
    httpAsyncClient.start()
    futureHttpAsyncClient.execute(post).andThen { case _ => httpAsyncClient.close() }.andThen {
      case Success(response) =>
        val statusCode: Int = response.getStatusLine.getStatusCode
        logger.info(s"Received response $statusCode: ${response.getStatusLine.getReasonPhrase}")
      case Failure(e) =>
        logger.error(s"Could not send a notification to endpoint ${pushSubscription.endpoint}", e)

    }
  }
}
