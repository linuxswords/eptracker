package controllers


import forms.SearchForm
import models.{TVShow, Media}
import play.api.cache.Cache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import request.EpisodeRequest.EpisodeAction
import java.net.URLDecoder
import play.api.Play.current

import controllers.Search.titleKeys
import scala.util.Random
import play.api.Play

/**
 *
 * @author knm
 */


object Show extends Controller with SearchForm
{
  val defaultPicturePath = "/var/www/eptracker/images/eptracker"

  def random = EpisodeAction{ implicit request =>
    val shows =Cache.getAs[List[TVShow]](titleKeys)
    shows match {
      case Some(showList) => {
        val randomShow = Random.shuffle(showList).head
        Redirect(routes.Show.show(randomShow.title))
      }
      case None => {
        val list= Media.allTitlesWithCount
        Cache.set(titleKeys, list)
        Redirect(routes.Show.random)
      }
    }
  }

  def showsByAbc = EpisodeAction{ implicit request =>
      val showMap = Media.showsByAbc()
    Ok(views.html.showsbyAbc(showMap))
  }

  def picture(name: String) = Action {
    val filePrefix = Play.current.configuration.getString("picture.storage").getOrElse(defaultPicturePath)

    Ok.sendFile(new java.io.File(filePrefix + "/" + name))
  }

  def epGuideData = EpisodeAction{ implicit request =>
    import play.api.Play.current
    val autocompleteData: Option[JsValue] = Cache.getAs[JsValue]("epguides")
    autocompleteData match {
      case Some(value) =>
      case None =>
      {
        util.Util.updateAutocompleteCache()
      }
    }
    Ok(Json.parse(autocompleteData.mkString))
  }

  def shows(page: Int = 0, sort: Int = 1) = EpisodeAction{ implicit request =>
    Ok(views.html.shows(Media.shows(page, 10, sort), sort))
  }

  def show(name: String, page: Int = 0, sort: Int = 1) = EpisodeAction{ implicit request =>
    val title = URLDecoder.decode(name, "UTF-8")
    Ok(views.html.show(Media.show(title, page, 10, sort), sort, name))
  }

  def recent() = EpisodeAction{ implicit request =>
    val allRecents = Media.recent(20)
    Ok(views.html.list("recent", allRecents))
  }

  def upcoming = EpisodeAction{ implicit request =>
    val allUpcomings = Media.upcoming(20)
    Ok(views.html.list("upcoming", allUpcomings))
  }

  def delete(name:String) = EpisodeAction { implicit request =>
    Media.delete(name)
    val returnUrl = request.headers.get(REFERER).getOrElse(routes.Application.index.url)
    Redirect(returnUrl)
  }

  def consumeAll(showId: String) = EpisodeAction{ implicit request =>
    Media.consumeAll(showId)
    val returnUrl = request.headers.get(REFERER).getOrElse(routes.Application.index.url)
    Redirect(returnUrl)
  }

  def consume(id:String, title:String, consume : Boolean) = EpisodeAction{ implicit request =>
    Media.consume(id, title, consume)
    Ok(consume.toString)
  }

  def update(name:String) = EpisodeAction{ implicit request =>
    Import.importShowIntoDb(name)
    val returnUrl = request.headers.get(REFERER).getOrElse(routes.Application.index.url)
    Redirect(returnUrl)
  }

  def episode(id:String) = TODO

  def deleteEpisode(id:String) = TODO

  def consumeEpisode(id:String, consume: Boolean = false) = TODO

  def all(page: Int = 0, sort: Int = 1) =  TODO //Action{
//    Ok(views.html.shows(Media.all(page, 10, sort), sort))
//  }


}
