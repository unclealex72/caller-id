package controllers

import auth.{DefaultEnv, User}
import call.{Call => _, _}
import cats.data._
import cats.implicits._
import com.mohiva.play.silhouette.api.{Authorization, Silhouette}
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.AuthInfoDAO
import contact.{ContactDao, ContactLoader}
import modem.ModemSender
import number.{NumberFormatter, PhoneNumberFactory}
import play.api.mvc.{Filters => _, _}

import scala.concurrent.{ExecutionContext, Future}

class Home(
            val numberLocationService: PhoneNumberFactory,
            val numberFormatter: NumberFormatter,
            val callDao: CallDao,
            val contactLoader: ContactLoader,
            val contactService: ContactDao,
            val maybeModemSender: Option[ModemSender],
            val silhouette: Silhouette[DefaultEnv],
            val authorization: Authorization[DefaultEnv#I, DefaultEnv#A],
            val authInfoDao: AuthInfoDAO[OAuth2Info],
            override val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext)
  extends AbstractController(controllerComponents) {

  def unauthorised = Action { implicit request =>
    Forbidden(views.html.goaway())
  }

  def index = silhouette.SecuredAction(authorization).async { implicit request =>
    request.identity.fullName match {
      case Some(name) =>
        callDao.calls(max = Some(20)).map { calls =>
          val callViews: Seq[CallView] = calls.flatMap(_.view)
          Ok(views.html.calls(name, callViews))
        }
      case None => Future.successful(Forbidden(""))
    }
  }

  def js = silhouette.SecuredAction(authorization) { implicit request =>
    Ok(views.js.contacts())
  }

  def updateContacts = silhouette.SecuredAction(authorization).async { implicit request =>
    val user: User = request.identity
    val upload: EitherT[Future, Seq[String], Int] = for {
      emailAddress <- EitherT(Future.successful(user.email.toRight(Seq("Email address required"))))
      accessToken <- EitherT(authInfoDao.find(user.loginInfo).map(_.toRight(Seq("Access token required"))))
      user <- EitherT.right(contactLoader.loadContacts(emailAddress, accessToken.accessToken))
      _ <- EitherT(contactService.upsertUser(user))
      persistedCallResponse <- EitherT(callDao.alterContacts(user.contacts))
    } yield {
      persistedCallResponse
    }
    upload.value.map {
      case Left(errs) => InternalServerError(errs.mkString("\n"))
      case Right(_) => NoContent
    }
  }

  def sendToModem = Action(parse.tolerantText(100)) { implicit request =>
    maybeModemSender match {
      case Some(modemSender) =>
        modemSender.send(request.body)
        NoContent
      case None =>
        NotFound
    }
  }

}

