package modules

import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import scaldi.Module
import slick.DatabaseProvider
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
 * Created by alex on 12/09/15.
 */
class PlayDatabaseProviderModule extends Module {
  bind[DatabaseProvider] to injected[ApplicationDatabaseProvider]
}

class ApplicationDatabaseProvider(application: Application) extends DatabaseProvider {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](application)
  val databaseProvider = DatabaseProvider(dbConfig.db)

  override def apply[R, S <: NoStream, E <: Effect](action: DBIOAction[R, S, E]): Future[R] = databaseProvider.apply(action)
}