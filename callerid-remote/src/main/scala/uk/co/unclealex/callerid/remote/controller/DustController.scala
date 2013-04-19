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

import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.List
import java.util.Map
import java.util.Set
import javax.annotation.PostConstruct
import org.eclipse.xtext.xbase.lib.Functions
import org.eclipse.xtext.xbase.lib.Procedures
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * A Spring controller that pre-compiles dust templates.
 * 
 * @author alex
 * 
 */
@Controller
class DustController {
    /**
    * The set of names of known templates to compile.
    */
    @Property var Set<String> templateNames

    /**
    * A map containing a compiled template for each template name.
    */
    @Property val Map<String, String> compiledTemplates = newHashMap()

    /**
    * The global scope for Rhino compilation.
    */
    @Property var Scriptable globalScope

    /**
     * Initialise and precompile all known scripts.
     */
    @PostConstruct
    def void precompile() {
        #["dust-full-1.2.2", "dust-helpers-1.1.1"].loadScripts
        templateNames.forEach [
            compiledTemplates.put(it, it.compile)
        ]
    }

    /**
    * Load the dust scripts into the global context
    */
    def void loadScripts(List<String> scriptNames) {
        withinJavascriptContext[
            optimizationLevel = 9
            globalScope = initStandardObjects
            scriptNames.forEach [ name |
                val scriptName = '''«name».js'''
                val Reader reader = '''public/js/«scriptName»'''.reader
                try {
                    evaluateReader(globalScope, reader, scriptName, 0, null);
                } finally {
                    reader.close
                }
            ]
        ]
    }

    /**
     * Convert a path into a readable class path resource.
     * @param path the path to convert.
     * @return 
     */
    def Reader reader(CharSequence path) {
        val InputStream in = typeof(DustController).classLoader.getResourceAsStream(path.toString)
        if (in == null) {
            throw new FileNotFoundException('''Cannot find a classpath resource for «path»''')
        } else {
            new InputStreamReader(in, Charsets::UTF_8)
        }
    }

    /**
     * Compile a dust template.
     * @param templateName the name of the template to compile.
     * @return The Javascript source of the compiled template.
     */
    def String compile(String templateName) {
        withinJavascriptContext[
            val Scriptable compileScope = newObject(globalScope)
            compileScope.parentScope = globalScope
            val StringBuilder sourceBuilder = new StringBuilder
            CharStreams::copy([|'''template/«templateName».dust'''.reader], sourceBuilder)
            compileScope.put("rawSource", compileScope, sourceBuilder.toString)
            compileScope.put("name", compileScope, templateName)
            evaluateString(compileScope, "(dust.compile(rawSource, name))", "JDustCompiler", 0, null) as String;
        ]
    }

    /**
    * Evaluate a function within a Javascript context.
    * @param function The function to evaluate.
    */
    def <S> S withinJavascriptContext(Functions$Function1<Context, S> function) {
        val Context context = Context::enter
        try {
            function.apply(context)
        } finally {
            Context::exit
        }
    }

    /**
    * Execute a procedure within a Javascript context.
    * @param procedure The procedure to execute.
    */
    def void withinJavascriptContext(Procedures$Procedure1<Context> procedure) {
        withinJavascriptContext[
            procedure.apply(it)
            null
        ]
    }

    /**
     * Serve a compiled dust template.
     */
    @RequestMapping(value="/template/{templateName}.js", produces="text/javascript")
    @ResponseBody
    def String template(@PathVariable("templateName") String templateName) {
        val String source = compiledTemplates.get(templateName)
        if (source == null) {
            throw new FileNotFoundException('''Cannot find a dust template called «templateName»''')
        } else {
            source
        }
    }
}
