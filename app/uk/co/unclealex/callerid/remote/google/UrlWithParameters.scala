package uk.co.unclealex.callerid.remote.google

import java.net.URLEncoder._
import java.net.URLDecoder._
import com.google.common.base.Charsets._
import java.net.URL
import com.google.common.base.Splitter
import com.google.common.base.Joiner
import java.net.URI
import scala.collection.JavaConversions._
/**
 * A class that represents a URL with its query parameters.
 */
case class UrlWithParameters(url: String, parameters: Map[String, _] = Map()) {

  /**
   * Add or update a parameters.
   * @param extraParameters The parameters to add or update.
   * @return A new {@link UrlWithParameters} with the extra parameters.
   */
  def withParameterMap(extraParameters: Map[String, _]): UrlWithParameters =
    new UrlWithParameters(url, parameters ++ extraParameters.mapValues { (v: Any) => v.toString })

  /**
   * Add or update a parameters.
   * @param extraParameters The parameters to add or update.
   * @return A new {@link UrlWithParameters} with the extra parameters.
   */
  def withParameters(parameter: Pair[String, _], extraParameters: Pair[String, _]*): UrlWithParameters =
    withParameterList(parameter :: extraParameters.toList)

  /**
   * Add or update a parameters.
   * @param extraParameters The parameters to add or update.
   * @return A new {@link UrlWithParameters} with the extra parameters.
   */
  def withParameterList(extraParameters: List[Pair[String, _]]): UrlWithParameters = {
    val parameters = extraParameters.foldLeft(Map[String, Any]())((m: Map[String, _], kv: Pair[String, _]) => m + kv)
    new UrlWithParameters(url, parameters)
  }

  /**
   * Convert to a plain URL.
   */
  def toURL: URL =
    new URL(
      if (parameters.isEmpty) {
        url
      } else {
        url + "?" + Joiner.on("&").withKeyValueSeparator("=").join(
          parameters.mapValues { (v: Any) => encode(v.toString, UTF_8.name) })
      })
}

/**
 * A class to parse a URL into a URL with parameters.
 */
object UrlWithParameters {

  /**
   * Implicit classes for URLs, URIs and strings.
   */
  abstract class UrlWithParametersAbstractImplicits[V](value: V) {

    def withParameterMap(parameters: Map[String, _]) = {
      UrlWithParameters.parse(value.toString).withParameterMap(parameters)
    }

    def withParameters(parameters: Pair[String, _]*) = {
      UrlWithParameters.parse(value.toString).withParameterList(parameters.toList)
    }
  }

  implicit class UrlWithParametersStringImplicits(value: String) extends UrlWithParametersAbstractImplicits[String](value)
  implicit class UrlWithParametersUrlImplicits(value: URL) extends UrlWithParametersAbstractImplicits[URL](value)
  implicit class UrlWithParametersUriImplicits(value: URI) extends UrlWithParametersAbstractImplicits[URI](value)

  def parse(url: URL): UrlWithParameters = parse(url.toString())

  def parse(url: String): UrlWithParameters = {
    val urlParts = splitOnce(url, '?')
    urlParts match {
      case (url, None) => new UrlWithParameters(url)
      case (url, Some(parameterString)) =>
        val parameters = parameterString.split('&')
        val parameterMap = parameters.map { param =>
          val kv = splitOnce(param, '=')
          kv._1 -> decode(kv._2.getOrElse(""), UTF_8.name)
        }.toMap
        new UrlWithParameters(url, parameterMap)
    }
  }

  private def splitOnce(str: String, splitOn: Char): Pair[String, Option[String]] = {
    val parts = str.split(splitOn)
    parts.length match {
      case 1 => parts(0) -> None
      case 2 => parts(0) -> Some(parts(1))
      case _ => throw new IllegalArgumentException(s"Cannot split string segment $str using separator $splitOn")
    }
  }
}

