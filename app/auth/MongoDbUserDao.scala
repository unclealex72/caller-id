package auth

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Give access to the user object.
  */
class MongoDbUserDao(override val reactiveMongoApi: ReactiveMongoApi) extends MongoDbDao(reactiveMongoApi, "user") with UserDao {

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val query = Json.obj("loginInfo" -> loginInfo)
    collection().flatMap(_.find(query).one[User])
  }

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val query = Json.obj("userID" -> userID)
    collection().flatMap(_.find(query).one[User])
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User)(implicit ec: ExecutionContext): Future[User] = {
    collection.flatMap(_.update(Json.obj("userID" -> user.userID), user, upsert = true)).map(_ => user)
  }
}
