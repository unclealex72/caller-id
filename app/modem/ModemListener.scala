/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @author unclealex72
 *
 */
package modem

import actors.ModemActor
import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import scaldi.Injector
import scaldi.akka.AkkaInjectable
import util.Provider

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ModemListener(val modem: Modem)(implicit injector: Injector, actorSystem: ActorSystem) extends StrictLogging with AkkaInjectable {

  val modemActor = injectActorRef[ModemActor]

  /**
   * Initialise the modem and then listen for calls.
   */
  def run: Future[Unit] = Future {
    modem.initialise()
    logger info "Initialisation succeeded."
    listenForCalls(modem)
  }

  /**
   * Listen for any calls and then notify the {@link #callController}.
   */
  def listenForCalls(modem: Modem): Unit = {
    modem.responses foreach { modemResponse =>
      modemActor ! modemResponse
    }
  }

  def pushResponse(modemResponse: ModemResponse): Unit = {
    modemActor ! modemResponse
  }
}




