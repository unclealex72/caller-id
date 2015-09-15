package controllers

import contact.ContactService
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
 * Created by alex on 15/09/15.
 */
class UserController(contactService: ContactService)(implicit ec: ExecutionContext) extends Controller{

  def user(email: String) = Action.async { implicit request =>
    for {
      created <- contactService.insertOrUpdateUser(email)
    }
    yield {
      if (created) Created("") else NoContent
    }
  }
}
