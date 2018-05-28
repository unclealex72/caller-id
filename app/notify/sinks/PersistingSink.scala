package notify.sinks

import call.{Call, PersistedCallDao, PersistedCallFactory}

import scala.concurrent.{ExecutionContext, Future}

class PersistingSink(
                      persistedCallFactory: PersistedCallFactory,
                      persistedCallDao: PersistedCallDao)(implicit ec: ExecutionContext) extends (Call => Future[_]) {

  override def apply(call: Call): Future[_] = {
    persistedCallDao.insert(persistedCallFactory.persist(call))
  }
}
