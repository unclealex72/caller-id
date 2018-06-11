package notify.sinks

import call.{Call, CallView}
import cats.data._
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}

abstract class CallViewSink(implicit ec: ExecutionContext) extends CallSink {

  override def consume(call: Call): Future[_] = {
    val transform: OptionT[Future, Unit] = for {
      view <- OptionT(Future.successful(call.view))
      _ <- OptionT.some[Future](consumeView(view))
    } yield {
      {}
    }
    transform.value
  }

  def consumeView(callView: CallView): Future[_]
}
