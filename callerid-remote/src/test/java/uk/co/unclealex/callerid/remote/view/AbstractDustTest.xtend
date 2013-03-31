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

import com.google.common.io.CharStreams
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.util.Map
import javax.xml.parsers.DocumentBuilderFactory
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Before
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.w3c.dom.Document
import org.xml.sax.InputSource

/**
 * A base class for tests regarding dust.js templates.
 */
abstract class AbstractDustTest {

    static var Scriptable GLOBAL_SCOPE

    @Property val String dustTemplateName

    new(String dustTemplateName) {
        this._dustTemplateName = dustTemplateName
    }

    @Before
    def void setupDust() {
        val Reader dustReader = new InputStreamReader(
            typeof(AbstractDustTest).classLoader.getResourceAsStream("public/js/dust-full-1.2.2.js"), "UTF-8")
        val Context dustEngineContext = Context::enter
        dustEngineContext.optimizationLevel = 9
        try {
            GLOBAL_SCOPE = dustEngineContext.initStandardObjects()
            dustEngineContext.evaluateReader(GLOBAL_SCOPE, dustReader, "dust-compile.js", 0, null)
        } finally {
            Context::exit
        }
        val StringBuilder templateBuilder = new StringBuilder;
        CharStreams::copy(
            [|
                new InputStreamReader(
                    typeof(AbstractDustTest).classLoader.getResourceAsStream(
                        '''template/«dustTemplateName».dust'''), "UTF-8")],
            templateBuilder
        )
        val String templateSource = templateBuilder.toString
        val Context dustContext = Context::enter()
        try {
            val Scriptable compileScope = dustContext.newObject(GLOBAL_SCOPE)
            compileScope.parentScope = GLOBAL_SCOPE
            compileScope.put("rawSource", compileScope, templateSource)
            compileScope.put("name", compileScope, dustTemplateName)
            dustContext.evaluateString(compileScope, "(dust.loadSource(dust.compile(rawSource, name)))", "JDustCompiler",
                0, null)
        } finally {
            Context::exit()
        }
    }

    def String render(Object context) {
        val Context dustContext = Context::enter()
        val Scriptable renderScope = dustContext.newObject(GLOBAL_SCOPE)
        renderScope.parentScope = GLOBAL_SCOPE

        val json = new ObjectMapper().writeValueAsString(context)
        val String renderScript = ('''
        {
          dust.render(
            name,
            JSON.parse(json),
            function( err, data) { 
              if(err) { writer.write(err);} else { writer.write( data ); }
            });
        }''')

        try {
            val StringWriter writer = new StringWriter
            renderScope.put("writer", renderScope, writer)
            renderScope.put("json", renderScope, json)
            renderScope.put("name", renderScope, dustTemplateName)

            dustContext.evaluateString(renderScope, renderScript, "JDustCompiler", 0, null);
            writer.toString

        } finally {
            Context::exit()
        }
    }

    def Document renderAsXml(Object context) {
        val String renderedString = render(context)
        DocumentBuilderFactory::newInstance.newDocumentBuilder.parse(new InputSource(new StringReader(renderedString)))
    }
}
