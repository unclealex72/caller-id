package uk.co.unclealex.callerid.remote.controller

import java.io.FileNotFoundException
import org.junit.Test

import static org.junit.Assert.*

/**
 * Test that dust templates can be compiled.
 */
class DustControllerTest {

    @Test
    def void testSimpleCompilation() {
        runTest("simple",
            '''(function(){dust.register("simple",body_0);function body_0(chk,ctx){return chk.write("Hello ").reference(ctx.get("world"),ctx,"h");}return body_0;})();''')
    }

    @Test
    def void testCompilationWithHelpers() {
        runTest("helper",
            '''(function(){dust.register("helper",body_0);function body_0(chk,ctx){return chk.helper("math",ctx,{},{"key":"16","method":"add","operand":"4"});}return body_0;})();''')
    }

    @Test(expected=typeof(FileNotFoundException))
    def void testMissing() {
        runTest("missing", '''''')
    }

    def void runTest(String templateName, String expectedCompilationResults) {
        new DustController => [
            templateNames = #{"simple", "helper"}
            precompile
            val String actualCompilationResults = template(templateName)
            assertEquals('''Template «templateName» did not compile correctly.''', expectedCompilationResults,
                actualCompilationResults)
        ]
    }
}
