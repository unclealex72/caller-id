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
 * @author alex
 *
 */
package uk.co.unclealex.callerid.remote.view

import java.io.InputStreamReader
import java.io.StringReader
import java.io.StringWriter
import scala.io.Source
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import com.fasterxml.jackson.databind.ObjectMapper
import javax.xml.parsers.DocumentBuilderFactory
import scala.xml.Document
import scala.xml.XML
import scala.xml.Elem

/**
 * A base class for tests regarding dust.js templates.
 */
abstract class AbstractDustTest(dustTemplateName: String) extends FunSuite with BeforeAndAfter {

  var GLOBAL_SCOPE: Scriptable = null

  before {
    val dustReader = new InputStreamReader(
      getClass.getClassLoader.getResourceAsStream("public/js/dust-full-1.2.2.js"), "UTF-8")
    val dustEngineContext = Context.enter
    dustEngineContext.setOptimizationLevel(9)
    try {
      GLOBAL_SCOPE = dustEngineContext.initStandardObjects()
      dustEngineContext.evaluateReader(GLOBAL_SCOPE, dustReader, "dust-compile.js", 0, null)
    } finally {
      Context.exit
    }
    val templateSource = Source.fromInputStream(
      getClass.getClassLoader.getResourceAsStream("public/js/dust-full-1.2.2.js")).getLines().mkString("\n")
    val dustContext = Context.enter
    try {
      val compileScope = dustContext.newObject(GLOBAL_SCOPE)
      compileScope.setParentScope(GLOBAL_SCOPE)
      compileScope.put("rawSource", compileScope, templateSource)
      compileScope.put("name", compileScope, dustTemplateName)
      dustContext.evaluateString(compileScope, "(dust.loadSource(dust.compile(rawSource, name)))", "JDustCompiler",
        0, null)
    } finally {
      Context.exit
    }
  }

  def render(context: Any): String = {
    val dustContext = Context.enter
    val renderScope = dustContext.newObject(GLOBAL_SCOPE)
    renderScope.setParentScope(GLOBAL_SCOPE)

    val json = new ObjectMapper().writeValueAsString(context)
    val renderScript = """
        {
          dust.render(
            name,
            JSON.parse(json),
            function( err, data) { 
              if(err) { writer.write(err);} else { writer.write( data ); }
            });
        }"""

    try {
      val writer = new StringWriter
      renderScope.put("writer", renderScope, writer)
      renderScope.put("json", renderScope, json)
      renderScope.put("name", renderScope, dustTemplateName)
      dustContext.evaluateString(renderScope, renderScript, "JDustCompiler", 0, null);
      writer.toString

    } finally {
      Context.exit
    }
  }

  def renderAsXml(context: Any): Elem = XML.loadString(render(context))
}
