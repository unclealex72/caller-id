import java.time.Instant

import cats.data.NonEmptyList
import play.api.libs.json._

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
}
