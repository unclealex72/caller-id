package push

import scala.concurrent.Future

/**
  * Persist browser push endpoints.
  */
trait PushEndpointDao {

  /**
    * Remove a stale push subscription.
    * @param endpoint The endpoint to remove.
    * @return
    */
  def remove(endpoint: String): Future[Unit]

  /**
    * Add or update a push subscription.
    *
    * @param pushSubscription The subscription to add or insert.
    * @return
    */
  def upsert(pushSubscription: PushSubscription): Future[Unit]

  /**
    * Get all stored push subscriptions.
    * @return
    */
  def all(): Future[Seq[PushSubscription]]
}
