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

  def search(text: String) = EpisodeAction{ implicit request =>
    val titleKeys: String = "storedShows"

    val boundedForm = searchForm.bindFromRequest
    boundedForm.fold(
    error =>  BadRequest,
    text => {
      val shows =Cache.getAs[List[TVShow]](titleKeys)
      val list = shows match {
        case None =>{
          val list= Media.allTitlesWithCount
          Cache.set(titleKeys, list)
          list
        }
        case Some(list) => list
      }

      val searchResult = list  filter{ case TVShow(title,_) => title.toLowerCase.contains(text.text.toLowerCase) }

      // different result pages for zero, one or n results
      searchResult match {
        case Nil => Ok(views.html.searchNoResult(text.text))
        case res :: Nil => Ok(views.html.show(Media.show(res.title), 0, res.title))
        case _ => Ok(views.html.searchresult(searchResult))
      }


    }
    )
  }

}
