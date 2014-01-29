package controllers

import forms.SearchForm
import play.api.mvc.{Action, Controller}
import play.api.cache._
import models.Media
import play.api.Play.current
import request.EpisodeRequest._
import models.TVShow
import scala.Some

/**
 *
 * @author knm
 */
object Search extends Controller with SearchForm {

  val titleKeys: String = "storedShows"

  def search(text: String) = EpisodeAction{ implicit request =>

    val boundedForm = searchForm.bindFromRequest
    boundedForm.fold(
    error =>  BadRequest,
    text => {
      val searchResult = Media.allTitlesWithCount  filter{ case TVShow(title,_) => title.toLowerCase.contains(text.text.toLowerCase) }

      // different result pages for zero, one or n results
      searchResult match {
        case Nil => Ok(views.html.searchNoResult(text.text))
        case singleResult :: Nil => Ok(views.html.show(Media.show(singleResult.title), 0, singleResult.title))
        case _ => Ok(views.html.searchresult(searchResult))
      }


    }
    )
  }

}
