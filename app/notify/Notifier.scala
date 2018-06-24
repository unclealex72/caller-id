package notify

import _root_.notify.sinks.CallSink
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl._
import call.{Call, CallService}
import cats.data.NonEmptyList
import com.typesafe.scalalogging.StrictLogging
import modem.Modem

import scala.concurrent.ExecutionContext

class Notifier(val modem: Modem,
               callService: CallService,
               sinkActions: NonEmptyList[CallSink])
              (implicit val actorSystem: ActorSystem, materializer: Materializer, ec: ExecutionContext) extends StrictLogging {

  logger.info("Starting modem notifications")

  val callsSource: Source[Call, modem.Disconnect] =
    modem.responses().
      mapAsync(16)(r => callService.call(r)).
      flatMapConcat {
        case Some(call) => Source.single(call)
        case None => Source.empty
      }

  val sinks: NonEmptyList[Sink[Call, NotUsed]] =
    sinkActions.map { originalAction =>
      val newAction: Call => Unit = call => {
        originalAction.consume(call)
      }
      newAction
    }.map(action => Sink.foreachParallel(16)(action).mapMaterializedValue(_ => NotUsed))

  val sink: Sink[Call, NotUsed] = {
    val firstSink: Sink[Call, NotUsed] = sinks.head
    sinks.tail match {
      case Nil => firstSink
      case secondSink :: otherSinks => Sink.combine(firstSink, secondSink, otherSinks :_*)(Broadcast(_))
    }
  }

  val _disconnect: modem.Disconnect = callsSource.to(sink).run()

  def disconnect(): Unit = {
    _disconnect.disconnect()
  }
}
