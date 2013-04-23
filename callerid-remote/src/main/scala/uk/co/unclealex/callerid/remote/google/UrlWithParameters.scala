package uk.co.unclealex.callerid.remote.google

import java.net.URLEncoder._
import java.net.URLDecoder._
import com.google.common.base.Charsets._
import java.net.URL
import com.google.common.base.Splitter
import scala.collection.JavaConversions._
import scala.collection.immutable.HashMap
import com.google.common.base.Joiner
/**
 * A class that represents a URL with its query parameters.
 */
case class UrlWithParameters(url: String, parameters: Map[String, String] = Map()) {

  /**
   * A class to parse a URL into a URL with parameters.
   */
  object UrlWithParameters {

    def parse(url: URL): UrlWithParameters = parse(url.toString())

    def parse(url: String): UrlWithParameters = {
      val urlParts = url.split('?')
      urlParts.length match {
        case 1 => new UrlWithParameters(urlParts(0))
        case 2 => new UrlWithParameters(
          urlParts(0), Splitter.on('&').omitEmptyStrings.withKeyValueSeparator("=").split(urlParts(1)).toMap)
        case _ => throw new IllegalArgumentException(s"Cannot parse URL $url")
      }
    }
  }

  /**
   * Add or update a parameters.
   * @param extraParameters The parameters to add or update.
   * @return A new {@link UrlWithParameters} with the extra parameters.
   */
  def withParameters(extraParameters: Pair[Any, Any]*): UrlWithParameters =
    withParameters(extraParameters.toMap)

  /**
   * Add or update a parameters.
   * @param extraParameters The parameters to add or update.
   * @return A new {@link UrlWithParameters} with the extra parameters.
   */
  def withParameters(extraParameters: Map[Any, Any]): UrlWithParameters =
    new UrlWithParameters(url, parameters ++ extraParameters.map { p => Pair(p._1.toString, p._2.toString) })

  /**
   * Convert to a plain URL.
   */
  def toURL: URL =
    new URL(
      if (parameters.isEmpty) {
        url
      } else {
        url + "?" + Joiner.on("&").withKeyValueSeparator("=").join(parameters.mapValues { encode(_, UTF_8.name) })
      })
}
