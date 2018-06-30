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

/**
  * The class that listens for modem response events and pushes them to a list of sinks.
  * @param modem       The [[Modem]] to listen to.
  * @param callService The [[CallService]] used to turn [[modem.ModemResponse]]s into [[Call]]s.
  * @param sinkActions The list of sink actions to send calls to.
  * @param actorSystem
  * @param materializer
  * @param ec
  */
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
