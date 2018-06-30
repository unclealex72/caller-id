import java.time.format.DateTimeFormatter
import java.time.{Instant, OffsetDateTime}

import cats.data.NonEmptyList
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

/**
  * Json codecs for various types
  */
package object json {

  /**
    * Merge two JSON structures. This is used to create MongoDB queries
    * @param a
    * @param b
    * @return
    */
  def merge(a: JsValue, b: JsObject): JsObject = {
    (a, b) match {
      case (oa : JsObject, ob: JsObject) =>
        ob.value.foldLeft(oa){ (result, kv) =>
          val (key, value) = kv
          val newValue: JsValue = (result.value.get(key), value) match {
            case (Some(sub :JsObject), ov : JsObject) => merge(sub, ov)
            case _ => value
          }
          result + (key -> newValue)
        }
      case _ => b
    }
  }

  implicit def nonEmptyListFormat[A](implicit listFormat: Format[List[A]]): Format[NonEmptyList[A]] = new Format[NonEmptyList[A]] {
    override def reads(json: JsValue): JsResult[NonEmptyList[A]] = listFormat.reads(json).flatMap {
      case Nil => JsError("list.empty")
      case x :: xs => JsSuccess(NonEmptyList(x, xs))
    }
    override def writes(o: NonEmptyList[A]): JsValue = listFormat.writes(o.toList)

  }

  implicit def offsetDateTimeFormat(implicit stringFormat: Format[String]): Format[OffsetDateTime] = new Format[OffsetDateTime] {
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override def reads(json: JsValue): JsResult[OffsetDateTime] = stringFormat.reads(json).flatMap { str =>
      Try(OffsetDateTime.parse(str, formatter)) match {
        case Success(dateTime) => JsSuccess(dateTime)
        case Failure(_) => JsError(JsonValidationError(s"Cannot parse '$str' as a date and time."))
      }
    }

    override def writes(odt: OffsetDateTime): JsValue = {
      stringFormat.writes(odt.format(formatter))
    }
  }
}
