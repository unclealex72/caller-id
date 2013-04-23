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
package uk.co.unclealex.callerid.remote.controller

import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import org.mozilla.javascript.Context
import org.springframework.stereotype.Controller
import java.io.Reader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.Charset
import com.google.common.base.Charsets
import scala.io.Source
import java.net.URL
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.PathVariable

/**
 * A Spring controller that pre-compiles dust templates.
 *
 * @author alex
 *
 */
@Controller
class DustController(templateNames: Set[String]) {

  /**
   * A map containing a compiled template for each template name.
   */
  val compiledTemplates: Map[String, String] = new HashMap

  /**
   * A small implicit class for transforming strings into classpath resources.
   */
  implicit class StringAsResourceImplicits(resourceName: String) {

    /**
     * Convert a string into a classpath resource URL.
     */
    def asResourceUrl: URL =
      Option.apply(getClass.getClassLoader.getResource(resourceName)).getOrElse(
        throw new FileNotFoundException(s"Cannot find resource ${resourceName}."))

    /**
     * Run code within a try-finally block with a classpath resource converted to a {@link Reader}.
     */
    def withResource(block: Reader => Unit) {
      val in = getClass.getClassLoader.getResourceAsStream(resourceName)
      if (in == null) {
        throw new FileNotFoundException(s"Cannot find resource ${resourceName}.")
      }
      val reader = new InputStreamReader(in, Charsets.UTF_8)
      try {
        block(reader)
      } finally {
        reader.close
      }
    }
  }

  /**
   * Run code within a Javascript context.
   */
  def withinContext(block: Context => Unit) {
    val context = Context.enter
    try {
      block(context)
    } finally {
      Context.exit
    }
  }

  /**
   * Precompile all known dust templates
   */
  withinContext { globalContext =>
    {
      globalContext.setOptimizationLevel(9)
      val globalScope = globalContext.initStandardObjects
      List("dust-full-1.2.2", "dust-helpers-1.1.1").foreach(scriptName => {
        s"public/js/${scriptName}.js".withResource(reader => {
          globalContext.evaluateReader(globalScope, reader, scriptName, 0, null)
        })
      })
      templateNames.foreach(templateName => {
        val compileScope = globalContext.newObject(globalScope)
        compileScope.setParentScope(globalScope)
        compileScope.put("rawSource", compileScope, Source.fromURL(s"template/${templateName}.dust".asResourceUrl).mkString)
        compileScope.put("name", compileScope, templateName)
        val compiledDustCode = globalContext.evaluateString(
          compileScope, "(dust.compile(rawSource, name))", "JDustCompiler", 0, null).asInstanceOf[String]
        compiledTemplates += Pair(templateName, compiledDustCode)
      })
    }
  }

  @RequestMapping(value = Array("/template/{templateName}.js"), produces = Array("text/javascript"))
  @ResponseBody
  def template(@PathVariable("templateName") templateName: String): String = {
    compiledTemplates.get(templateName).getOrElse(throw new FileNotFoundException(s"Cannot find dust template ${templateName}."))
  }
}
