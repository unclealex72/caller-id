package actors

import akka.actor.Actor
import scaldi.Injector
import scaldi.akka.AkkaInjectable

/**
 * Created by alex on 01/09/15.
 */
class ParentActor(implicit inj: Injector) extends Actor with AkkaInjectable {

  val loggingActor = injectActorRef[LoggingActor]
  val squeezeboxActor = injectActorRef[SqueezeboxActor]
  val actors = Seq(loggingActor, squeezeboxActor)

  override def receive: Receive = {
    case msg => actors foreach { actor =>
      actor ! msg
    }
  }
}
