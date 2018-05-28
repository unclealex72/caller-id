package number

import java.io.FileNotFoundException

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json._

import scala.io.{Codec, Source}

case class Countries(countries: List[Country])
object Countries extends StrictLogging {

  def apply(): Countries = parseJson("countries.json")
  def parseJson(resourceName: String): Countries = {
    Option(getClass.getClassLoader.getResource(resourceName)) match {
      case Some(url) =>
        val source = Source.fromURL(url)(Codec.UTF8)
        try {
          logger info s"Loading countries information from $url"
          countriesReads.reads(Json.parse(source.mkString)) match {
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

  implicit val countriesReads: Reads[Countries] = Json.reads[Countries]
  implicit val countriesWrites: Writes[Countries] = Json.writes[Countries]

}