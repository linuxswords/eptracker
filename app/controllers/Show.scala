package controllers


import forms.SearchForm
import models.Media
import play.api.data
import play.api.data.Form
import data.Form
import data.Forms._
import play.api.cache.Cache
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Action, Controller}


/**
 *
 * @author knm
 */


object Show extends Controller with SearchForm
{
  val importForm = Form(
  "epGuideId" -> nonEmptyText
  )

  def showsByAbc = Action{ implicit request =>
      val recent = Media.recent()
      val upcoming = Media.upcoming()
      implicit val sidebarItems =  (recent, upcoming)
      val showMap = Media.showsByAbc()
    Ok(views.html.showsbyAbc(showMap, importForm))
  }


  def epGuideData = Action{
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

  def shows(page: Int = 0, sort: Int = 1) = Action{ implicit request =>
    val recent = Media.recent()
    val upcoming = Media.upcoming()
    implicit val sidebarItems =  (recent, upcoming)
    Ok(views.html.shows(Media.shows(page, 10, sort), sort, importForm))
  }

  def show(name: String, page: Int = 0, sort: Int = 1) = Action {  implicit request =>
    val recent = Media.recent()
    val upcoming = Media.upcoming()
    implicit val sidebarItems =  (recent, upcoming)
    Ok(views.html.show(Media.show(name, page, 10, sort), sort, name, importForm))
  }

  def recent() = Action { implicit request =>
    val recent = Media.recent()
    val upcoming = Media.upcoming()
    implicit val sidebarItems =  (recent, upcoming)
    val allRecents = Media.recent(10)
    Ok(views.html.list("recent", allRecents, importForm))
  }

  def upcoming = Action { implicit request =>
    val recent = Media.recent()
    val upcoming = Media.upcoming()
    implicit val sidebarItems =  (recent, upcoming)
    val allUpcomings = Media.upcoming(10)
    Ok(views.html.list("upcoming", allUpcomings, importForm))
  }

  def delete(name:String) = Action {
    Media.delete(name)
    shows()
  }

  def consumeAll(showId: String) = Action {  request =>
    Media.consumeAll(showId)
    val returnUrl = request.headers.get(REFERER).getOrElse(routes.Application.index.url)
    Redirect(returnUrl)
  }

  def consume(id:String, title:String, consume : Boolean) = Action {  implicit request =>
    Media.consume(id, title, consume)
    Ok(consume.toString)
  }

  def update(name:String) = Action { request =>
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
