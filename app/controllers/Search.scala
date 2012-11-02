package controllers

import forms.SearchForm
import play.api.mvc.{Action, Controller}

/**
 *
 * @author knm
 */
object Search extends Controller with SearchForm {

  def search(text: String) = Action { implicit request =>

    val boundedForm = searchForm.bindFromRequest
    boundedForm.fold(
    error =>  BadRequest,
      text => {  Ok(text.text) }
    )
  }

}
