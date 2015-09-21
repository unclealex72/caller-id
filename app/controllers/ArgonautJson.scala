package controllers

import argonaut.Argonaut._
import argonaut._
import play.api.http.Status._
import play.api.http.{ContentTypeOf, ContentTypes, LazyHttpErrorHandler, Writeable}
import play.api.mvc.{BodyParsers, Codec, Results}

import scala.concurrent.ExecutionContext
import scalaz.{-\/, \/-}

/**
 * Created by alex on 20/09/15.
 */
object ArgonautJson extends Results with BodyParsers {

  implicit def writeableOf_ArgonautJson(implicit codec: Codec, ec: ExecutionContext): Writeable[argonaut.Json] = {
    Writeable(jsval => codec.encode(jsval.toString()))
  }

  implicit def writeableOf_JsonType[T](implicit codec: Codec, ec: ExecutionContext, encodeJson: EncodeJson[T]) =
  writeableOf_ArgonautJson.map(encodeJson.apply)

  implicit def contentTypeOf_ArgonautJson(implicit codec: Codec, ec: ExecutionContext): ContentTypeOf[argonaut.Json] = {
    ContentTypeOf[argonaut.Json](Some(ContentTypes.JSON))
  }

  def parser[T](implicit decodeJson: DecodeJson[T], ec: ExecutionContext) =
    parse.when(
      _.contentType.exists(m => m.equalsIgnoreCase("text/json") || m.equalsIgnoreCase("application/json")),
      tolerantParser(decodeJson, ec),
      request => LazyHttpErrorHandler.onClientError(request, UNSUPPORTED_MEDIA_TYPE, "Expecting text/json or application/json body"))

  def tolerantParser[T](implicit decodeJson: DecodeJson[T], ec: ExecutionContext) = parse.tolerantText.map(Parse.decodeEither[T]).validate {
    case -\/(error) => Left(BadRequest(Json("error" -> jString(error))))
    case \/-(value) => Right(value)
  }

}
