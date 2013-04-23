package uk.co.unclealex.callerid.remote.controller

import org.scalatest.FunSuite
import org.scalatest.matchers.Matcher
import org.scalatest.matchers.ShouldMatchers
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.FileNotFoundException

/**
 * Test that dust templates can be compiled.
 */
class DustControllerTest extends FunSuite with ShouldMatchers {

  val dustController = new DustController(Set("simple", "helper"))

  test("simple compilation") {
    "simple" should compileTo(
      """(function(){dust.register("simple",body_0);function body_0(chk,ctx){return chk.write("Hello ").reference(ctx.get("world"),ctx,"h");}return body_0;})();""")
  }

  test("compilation with helpers") {
    "helper" should compileTo(
      """(function(){dust.register("helper",body_0);function body_0(chk,ctx){return chk.helper("math",ctx,{},{"key":"16","method":"add","operand":"4"});}return body_0;})();""")
  }

  test("missing") {
    intercept[FileNotFoundException] {
      "missing" should compileTo("""""")
    }
  }

  def compileTo(expectedCompilationResults: String): Matcher[String] = {
    new Matcher[String]() {
      override def apply(left: String) = {
        val actualCompilationResults = dustController.template(left)
        equal(expectedCompilationResults).apply(actualCompilationResults)
      }
    }
  }
}
