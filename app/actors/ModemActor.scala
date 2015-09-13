package actors

import akka.actor.Actor
import call.ReceivedCallFactory
import modem.{Number, Withheld}
import scaldi.Injector
import scaldi.akka.AkkaInjectable

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by alex on 01/09/15.
 */
class ModemActor(implicit inj: Injector, ec: ExecutionContext) extends Actor with AkkaInjectable {

  val forwardActors = Seq(injectActorRef[ModemLoggingActor])
  val callActors = Seq(injectActorRef[CallLoggingActor], injectActorRef[SqueezeboxActor])
  val receivedCallFactory = inject[ReceivedCallFactory]

  override def receive: Receive = {
    case msg =>
      forwardActors foreach { actor => actor ! msg }
      val receivedCallsFuture = msg match {
        case Number(number) => receivedCallFactory.create(Some(number)).map(Some(_))
        case Withheld => receivedCallFactory.create(None).map(Some(_))
        case _ => Future { None }
      }
      receivedCallsFuture.map { receivedCalls =>
        for {
          receivedCall <- receivedCalls
          actor <- callActors
        } {
          actor ! receivedCall
        }
      }
  }
}
