package auth

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import persistence.MongoDbDao
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._

import scala.concurrent.{ExecutionContext, Future}

class MongoDbOauth2AuthInfoDao(val reactiveMongoApi: ReactiveMongoApi)
                              (implicit executionContext: ExecutionContext) extends DelegableAuthInfoDAO[OAuth2Info] {

  trait LoginInfoQuery {
    def query(loginInfo: LoginInfo): JsObject
  }
  private val dao = new MongoDbDao(reactiveMongoApi, "oauth2") with LoginInfoQuery {

    def query(loginInfo: LoginInfo): JsObject =
      "loginInfo.providerID" === loginInfo.providerID && "loginInfo.providerKey" === loginInfo.providerKey
  }

  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = {
    for {
      info <- dao.collection()
      cursor = info.find(dao.query(loginInfo)).cursor[LoginInfoAndOAuth2Info]()
      maybeResult <- cursor.headOption
    } yield {
      maybeResult.map(_.oAuth2Info)
    }
  }

  def upsert(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    for {
      info <- dao.collection()
      _ <- info.update(dao.query(loginInfo), LoginInfoAndOAuth2Info(loginInfo, authInfo), upsert = true)
    } yield {
      authInfo
    }
  }

  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = upsert(loginInfo, authInfo)

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = upsert(loginInfo, authInfo)

  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = upsert(loginInfo, authInfo)

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    for {
      info <- dao.collection()
      _ <- info.remove(dao.query(loginInfo))
    } yield {
      {}
    }
  }
}

case class LoginInfoAndOAuth2Info(loginInfo: LoginInfo, oAuth2Info: OAuth2Info)

object LoginInfoAndOAuth2Info {

  private implicit val oAuth2InfoFormat: OFormat[OAuth2Info] = Json.format[OAuth2Info]
  implicit val loginInfoFormat: OFormat[LoginInfo] = Json.format[LoginInfo]
  implicit val loginInfoAndOAuth2InfoFormat: OFormat[LoginInfoAndOAuth2Info] = Json.format[LoginInfoAndOAuth2Info]
}