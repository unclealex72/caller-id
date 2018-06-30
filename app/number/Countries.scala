package number

import java.io.FileNotFoundException

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json._

import scala.io.{BufferedSource, Codec, Source}

/**
  * A container for all countries. This class allows all country information to be loaded from a JSON resource.
  * @param countries All known countries.
  */
case class Countries(countries: Seq[Country])

object Countries extends StrictLogging {

  /**
    * Parse the default countries resource, <code>countries.json</code>.
    * @return All known country information.
    */
  def apply(): Countries = parseJson("countries.json")

  implicit val countriesFormat: Format[Countries] = Json.format[Countries]

  /**
    * Parse a resource
    * @param resourceName The name of the resource.
    * @return The [[Countries]] instance created by parsing the resource.
    */
  def parseJson(resourceName: String): Countries = {
    Option(getClass.getClassLoader.getResource(resourceName)) match {
      case Some(url) =>
        val source: BufferedSource = Source.fromURL(url)(Codec.UTF8)
        try {
          logger info s"Loading countries information from $url"
          Json.fromJson(Json.parse(source.mkString)) match {
            case JsSuccess(countries, _) => countries
            case JsError(_) => throw new IllegalStateException("Cannot parse STD code information")
          }
        }
        finally {
          source.close()
        }
      case None => throw new FileNotFoundException("Cannot find resource countries.json")
    }

  }
}