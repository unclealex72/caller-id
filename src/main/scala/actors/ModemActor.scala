package actors

import akka.actor.Actor
import call.ReceivedCallFactory
import modem.{Number, Withheld}
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
 * Created by alex on 01/09/15.
 */
class ModemActor(implicit inj: Injector) extends Actor with AkkaInjectable {

  val forwardActors = Seq(injectActorRef[ModemLoggingActor])
  val callActors = Seq(injectActorRef[CallLoggingActor], injectActorRef[SqueezeboxActor])
  val receivedCallFactory = inject[ReceivedCallFactory]

  override def receive: Receive = {
    case msg => {
      forwardActors foreach { actor => actor ! msg }
      val numbers = msg match {
        case Number(number) => Some(receivedCallFactory.create(Some(number)))
        case Withheld => Some(receivedCallFactory.create(None))
        case _ => None
      }
      for {
        number <- numbers
        actor <- callActors
      } {
        actor ! number
      }
    }
  }
}
