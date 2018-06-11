package notify.sinks

import call.{Call, CallDao}

import scala.concurrent.{ExecutionContext, Future}

class PersistingSink(callDao: CallDao)(implicit ec: ExecutionContext) extends CallSink {

  override def consume(call: Call): Future[_] = {
    callDao.insert(call)
  }
}
