package uk.co.unclealex.callerid.call

import org.junit.Test
import org.junit.Assert._
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.ObjectMapper

class RemoteConfigurationTest extends FunSuite with ShouldMatchers {

  test("A remote configuration object can be deserialised from JSON")({
    var json = """{
            "username" : "Brian",
            "password" : "Br1an",
            "url" : "https://www.somewhere.com/api"
        }"""
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val reader = mapper.reader(classOf[RemoteConfiguration])
    val actualConfiguration: RemoteConfiguration = reader.readValue(json)
    val expectedConfiguration = RemoteConfiguration("https://www.somewhere.com/api", username = "Brian", password = "Br1an")
    actualConfiguration should equal(expectedConfiguration)
  })

}