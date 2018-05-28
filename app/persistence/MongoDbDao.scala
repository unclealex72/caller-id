package persistence

import java.time.Instant

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.{MultiBulkWriteResult, WriteError, WriteResult}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class MongoDbDao(
                  val reactiveMongoApi: ReactiveMongoApi,
                  val collectionName: String) {

  def database()(implicit ec: ExecutionContext): Future[DefaultDB] = reactiveMongoApi.database

  def collection()(implicit ec: ExecutionContext): Future[JSONCollection] =
    database().map(_.collection[JSONCollection](collectionName))

  case class WriteResponse(ok: Boolean, writeErrors: Seq[WriteError]) {
    def toEither: Either[Seq[String], Unit] = {
      if (ok) {
        Right({})
      }
      else {
        Left(writeErrors.map(err => s"${err.index}-${err.code}: ${err.errmsg}"))
      }

    }
  }

  implicit def writeResultToWriteResponse(writeResult: WriteResult): WriteResponse = WriteResponse(writeResult.ok, writeResult.writeErrors)
  implicit def multiBulkWriteResultToWriteResponse(multiBulk: MultiBulkWriteResult): WriteResponse = WriteResponse(multiBulk.ok, multiBulk.writeErrors)

  implicit class JsonQueryImplicits(jsObject: JsObject) {


    def &&(other: JsObject): JsObject = {
      json.merge(jsObject, other) match {
        case obj : JsObject => obj
        case _ => throw new IllegalStateException("Result of a merge was not an object.")
      }
    }
  }

  trait JsonComparable {
    def toLong(): Long
  }

  implicit val longToJsonComparable: Long => JsonComparable = l => () => l
  implicit val instantToJsonComparable: Instant => JsonComparable = i => () => i.toEpochMilli
  implicit def optionToJsonComparable[A](oa: Option[A])(implicit toJsonComparable: A => JsonComparable): Option[JsonComparable] = {
    oa.map(toJsonComparable)
  }

  implicit class SortImplicits(field: String) {

    private def sort(ordering: Long): JsObject = Json.obj(field -> JsNumber(ordering))
    def asc: JsObject = sort(1)
    def desc: JsObject = sort(-1)
  }

  implicit class QueryImplicits(field: String) {

    def ===(value: String): JsObject = Json.obj(field -> JsString(value))

    private def compare(comparator: String, value: JsonComparable): JsObject = {
      Json.obj(field -> Json.obj(comparator -> JsNumber(value.toLong())))
    }

    private def elvis(maybeValue: Option[JsonComparable], compare: JsonComparable => JsObject): JsObject = {
      maybeValue.map(compare).getOrElse(JsObject.empty)
    }

    def >(value: JsonComparable): JsObject = compare("$gt", value)
    def >=(value: JsonComparable): JsObject = compare("$gte", value)
    def <(value: JsonComparable): JsObject = compare("$lt", value)
    def <=(value: JsonComparable): JsObject = compare("$lte", value)
    def ?>(maybeValue: Option[JsonComparable]): JsObject = elvis(maybeValue, >)
    def ?>=(maybeValue: Option[JsonComparable]): JsObject = elvis(maybeValue, >=)
    def ?<(maybeValue: Option[JsonComparable]): JsObject = elvis(maybeValue, <)
    def ?<=(maybeValue: Option[JsonComparable]): JsObject = elvis(maybeValue, <=)

  }
}
