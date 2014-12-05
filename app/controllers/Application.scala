package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Show.showsByAbc())
  }

  def ang = Action{
    Ok(views.html.ang.index())
  }
  
}

