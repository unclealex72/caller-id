package controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def register = Action {
    Ok(views.html.register())
  }

}
