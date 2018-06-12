import java.time.format.DateTimeFormatter
import java.time.{Instant, OffsetDateTime}

import cats.data.NonEmptyList
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

/**
  * Json codecs for various types
  */
package object json {

  def merge(a: JsValue, b: JsObject): JsObject = {
    (a, b) match {
      case (oa : JsObject, ob: JsObject) =>
        ob.value.foldLeft(oa){ (result, kv) =>
          val (key, value) = kv
          val newValue = (result.value.get(key), value) match {
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

  implicit def instantFormat(implicit longFormat: Format[Long]): Format[Instant] = new Format[Instant] {
    override def reads(json: JsValue): JsResult[Instant] = longFormat.reads(json).map(Instant.ofEpochMilli)
    override def writes(instant: Instant): JsValue = longFormat.writes(instant.toEpochMilli)
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
