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
 *   http://www.apache.org/licenses/LICENSE-2.0
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

package legacy.remote.contact

import scala.collection.immutable.Map
import legacy.remote.number.PhoneNumber
import java.util.concurrent.atomic.AtomicReference
import java.util.TimerTask
import java.util.Timer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * @author alex
 *
 */
class CachingContactService(contactService: ContactService, updateFrequency: Long) extends ContactService {

  /**
   * A cache used to hold contacts garnered from Google.
   */
  val cache = new AtomicReference[Map[String, Contact]]

  /**
   * The time used to periodically update the timer.
   */
  val updateTimer = new Timer

  /**
   * Prepopulate the cache and set off a task to update it periodically
   */
  @PostConstruct
  def initialise: Unit = {
    val updateTimerTask = new TimerTask {
      override def run: Unit = populateCache
    }
    updateTimerTask.run
    updateTimer.scheduleAtFixedRate(updateTimerTask, updateFrequency, updateFrequency)
  }

  /**
   * Populate the cache
   */
  def populateCache {
    val contactsByPhoneNumber = contactService.contactsByNormalisedPhoneNumber
    cache.set(contactsByPhoneNumber)
  }

  def contactsByNormalisedPhoneNumber: Map[String, Contact] = {
    cache.get
  }

  /**
   * Shutdown the cache populator
   */
  @PreDestroy
  def shutdown {
    updateTimer.cancel
  }
}