package persistence

import java.net.ServerSocket

import com.github.simplyscala.MongoEmbedDatabase
import com.typesafe.config.ConfigFactory
import org.scalatest.{FutureOutcome, Matchers, fixture}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.gridfs.GridFS
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.JSONSerializationPack

import scala.concurrent.Future

abstract class MongoDbDaoSpec[DAO <: MongoDbDao](val collectionName: String) extends fixture.AsyncWordSpec with Matchers with MongoEmbedDatabase {

  type FixtureParam = DaoAndDb

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val serverSocket = new ServerSocket(0)
    val port = try {
      serverSocket.getLocalPort
    } finally serverSocket.close()
    val mongodProps = mongoStart(port)
    val mongoDriver = MongoDriver(ConfigFactory.empty())
    val mongoConnection = mongoDriver.connection(List(s"localhost:$port"))
    val mongoDatabase = for {
      db <- mongoConnection.database("test")
      _ <- db.collection[BSONCollection](collectionName).
        insert(ordered = true).
        many(initialData())
    } yield db

    val reactiveMongoApi = new ReactiveMongoApi {
      override def driver: MongoDriver = mongoDriver
      override def connection: MongoConnection = mongoConnection
      override def database: Future[DefaultDB] = mongoDatabase
      override def asyncGridFS: Future[GridFS[JSONSerializationPack.type]] =
        throw new NotImplementedError("asyncGridFS")
      override def db: DefaultDB = throw new NotImplementedError("db")
      override def gridFS: GridFS[JSONSerializationPack.type] =
        throw new NotImplementedError("gridFS")
    }
    val param: FixtureParam = DaoAndDb(createDao(reactiveMongoApi), mongoDatabase)
    withFixture(test.toNoArgAsyncTest(param)).onCompletedThen { _ =>
      mongoDriver.close()
      mongoStop(mongodProps)
    }
  }

  case class DaoAndDb(dao: DAO, db: Future[DefaultDB])

  def initialData(): Seq[BSONDocument]

  def createDao(reactiveMongoApi: ReactiveMongoApi): DAO
}
