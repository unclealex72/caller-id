package actors

import akka.actor.Actor
import call.ReceivedFactory
import modem.{Ring, Number, Withheld}
import scaldi.Injector
import scaldi.akka.AkkaInjectable

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by alex on 01/09/15.
 */
class ModemActor(implicit inj: Injector, ec: ExecutionContext) extends Actor with AkkaInjectable {

  val forwardActors = Seq(injectActorRef[ModemLoggingActor])
  val callActors = Seq(injectActorRef[CallLoggingActor], injectActorRef[SqueezeboxActor])
  val receivedFactory = inject[ReceivedFactory]

  override def receive: Receive = {
    case msg =>
      forwardActors foreach { actor => actor ! msg }
      val receivedsFuture = msg match {
        case Number(number) => receivedFactory.create(Some(number)).map(Some(_))
        case Withheld => receivedFactory.create(None).map(Some(_))
        case Ring => receivedFactory.ring.map(Some(_))
        case _ => Future { None }
      }
      receivedsFuture.map { receiveds =>
        for {
          received <- receiveds
          actor <- callActors
        } {
          actor ! received
        }
      }
  }
}
