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

package uk.co.unclealex.callerid.squeezebox

import scala.collection.mutable.ListBuffer

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import uk.co.unclealex.callerid.device.Device
import uk.co.unclealex.process.packages.PackagesRequired

/**
 * @author alex
 *
 */
class SqueezeboxImplTest extends FunSuite with ShouldMatchers with MockFactory {

  test("query command") {
    runCommandTest("player count ?", Some("player count 2"), Some("2"));
  }

  test("non-query command") {
    runCommandTest("xx display", Some("ok"), Some("ok"));
  }

  test("null command") {
    runCommandTest("xx display", None, None);
  }

  test("display to two squeezeboxes") {
    val responses = Map(
      "player count ?" -> "player count 2",
      "player id 0 ?" -> "player id 0 00:11",
      "player id 1 ?" -> "player id 1 00:22")
    val device = new MapDevice(responses)
    val squeezebox = new SqueezeboxImpl(device)
    squeezebox.displayText("Top Line", "Bottom Line!")
    device.commands.toSeq should equal(List(
      "player count ?",
      "player id 0 ?",
      "00:11 display Top%20Line Bottom%20Line%21 30",
      "player id 1 ?",
      "00:22 display Top%20Line Bottom%20Line%21 30").toSeq)
  }

  def runCommandTest(command: String, response: Option[String], expectedResult: Option[String]) {
    val device = stub[Device]
    (device.readLine _).when().returning(response)
    val squeezebox = new SqueezeboxImpl(device)
    val actualResult = squeezebox.execute(command);
    (device.writeLine _).verify(command)
    (device.readLine _).verify()
    actualResult should equal(expectedResult)
  }

}

class MapDevice(responses: Map[String, String]) extends Device {

  var nextResponse: Option[String] = None
  val commands: ListBuffer[String] = ListBuffer()

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
