/**
 * Copyright 2012 Alex Jones
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
 * @author alex
 *
 */

package squeezebox

import device.IoDevice
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import util.Provider

import scala.collection.mutable.ListBuffer

/**
 * @author alex
 *
 */
class SqueezeboxImplSpec extends Specification with Mockito {

  "a query command" should {
    "should only return the query value" in
      runCommandTest("player count ?", Some("player count 2"), Some("2"))
  }

  "a non-query command" should {
    "should echo the whole response" in
      runCommandTest("xx display", Some("ok"), Some("ok"))
  }

  "a null command" should {
    "return nothing" in
      runCommandTest("xx display", None, None)
  }

  "displaying to two squeezeboxes" should {
    "send the same commands to each squeezebox" in {
      val responses = Map(
        "player count ?" -> "player count 2",
        "player id 0 ?" -> "player id 0 00:11",
        "player id 1 ?" -> "player id 1 00:22",
        "exit" -> "")
      val device = new MapDevice(responses)
      val provider = Provider.singleton[IoDevice](device)
      val squeezebox = new SqueezeboxImpl(provider)
      squeezebox.displayText("Top Line", "Bottom Line!")
      device.commands.toSeq must be equalTo Seq(
        "player count ?",
        "player id 0 ?",
        "00:11 display Top%20Line Bottom%20Line%21 30",
        "player id 1 ?",
        "00:22 display Top%20Line Bottom%20Line%21 30",
        "exit")
    }
  }

  def runCommandTest(command: String, response: Option[String], expectedResult: Option[String]) = {
    val device = mock[IoDevice]
    device.readLine returns response
    val squeezebox = new SqueezeboxImpl(Provider.singleton[IoDevice](device))
    val actualResult = squeezebox.execute(command)(device)
    actualResult must be equalTo expectedResult
    there was one(device).writeLine(command)
  }

}

class MapDevice(responses: Map[String, String]) extends IoDevice {

  val commands: ListBuffer[String] = ListBuffer()
  var nextResponse: Option[String] = None

  override def writeLine(command: String) = {
    nextResponse = responses.get(command)
    commands += command
  }

  override def readLine = {
    val response = nextResponse
    nextResponse = None
    response
  }

  override def close {
    // Do nothing.
  }
}
