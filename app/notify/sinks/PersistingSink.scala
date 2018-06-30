package notify.sinks

import call.{Call, CallDao}

import scala.concurrent.{ExecutionContext, Future}

/**
  * A sink that persists calls into a data store.
  * @param callDao The [[CallDao]] used to store the call.
  * @param ec
  */
class PersistingSink(callDao: CallDao)(implicit ec: ExecutionContext) extends CallSink {

  override def consume(call: Call): Future[_] = {
    callDao.insert(call)
  }
}
