package notify.sinks

import call.{Call, CallDao}

import scala.concurrent.{ExecutionContext, Future}

class PersistingSink(callDao: CallDao)(implicit ec: ExecutionContext) extends (Call => Future[_]) {

  override def apply(call: Call): Future[_] = {
    callDao.insert(call)
  }
}
