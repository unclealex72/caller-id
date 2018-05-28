import java.time.Instant

import cats.data.NonEmptyList
import play.api.libs.json._

/**
  * Json codecs for various types
  */
package object json {

  def merge(a: JsValue, b: JsValue): JsValue = {
    (a, b) match {
      case (oa : JsObject, ob: JsObject) =>
        ob.value.foldLeft(oa){ (result, kv) =>
          val (key, value) = kv
          val newValue = (result.value.get(key), value) match {
            case (Some(sub :JsObject), ov : JsObject) => sub ++ ov
            case _ => value
          }
          result + (key -> newValue)
        }
      case _ => b
    }
  }

  implicit def nonEmptyListReads[A](implicit listReads: Reads[List[A]]): Reads[NonEmptyList[A]] = {
    listReads.flatMap {
      case Nil => (_: JsValue) => JsError("empty.list")
      case x :: xs => Reads.pure(NonEmptyList.of(x, xs :_*))
    }
  }

  implicit def nonEmptyListWrites[A](implicit listWrites: Writes[List[A]]): Writes[NonEmptyList[A]] = (o: NonEmptyList[A]) => {
    listWrites.writes(o.toList)
  }

  implicit def instantReads(implicit longReads: Reads[Long]): Reads[Instant] = {
    longReads.map(millis => Instant.ofEpochMilli(millis))
  }

  implicit def instantWrites(implicit longWrites: Writes[Long]): Writes[Instant] = (instant: Instant) => {
    longWrites.writes(instant.toEpochMilli)
  }
}
