package slick

import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

/**
 * Created by alex on 07/09/15.
 */
trait DatabaseProvider {

  def apply[R, S <: NoStream, E <: Effect](action: DBIOAction[R, S, E]): Future[R]
}

object DatabaseProvider {

  def apply(db: JdbcBackend#DatabaseDef): DatabaseProvider = new DatabaseProvider {
    override def apply[R, S <: NoStream, E <: Effect](action: DBIOAction[R, S, E]): Future[R] = db.run(action)
  }
}