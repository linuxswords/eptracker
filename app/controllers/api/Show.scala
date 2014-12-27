package controllers.api

import java.net.URLDecoder

import controllers.{routes, Import}
import controllers.Search._
import controllers.Show._
import models.ShowStat.showStatFormats
import models.{Media, TVShow}
import play.api.{Logger, Play}
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.util.Random
/**
  * @author knm
  */
object Show extends Controller{

   def consumedStat = Action{
     val showMap = Media.consumeStati
     Ok(Json.toJson(showMap))
   }

   def torrentEngine = Action{
     val torrentEngine = Play.configuration.getString("torrent.searchstring")
     Ok(torrentEngine.getOrElse("no torrent engine configured! add it to the application.conf with the key 'torrent.searchstring'"))
   }

   def recent = Action{
     val recent = Media.recent(20)
     Ok(Json.toJson(recent))
   }

   def upcoming = Action{
     val upcoming = Media.upcoming(20)
     Ok(Json.toJson(upcoming))
   }

   def shows(page: Int = 0, sort: Int = 1) = Action{
     val shows = Media.showsWithoffset(page, 10)
     Ok (Json.toJson(shows))
   }


   def random = Action{
     val shows = lazyInitTitleCache
     val randomShow = Random.shuffle(shows).head
     Ok(Json.toJson(randomShow.title))
   }

   def showsByAbc = Action{
     val showMap = Media.showsByAbc()
 //    Ok(Json.toJson(showMap))
     Ok("foo")
   }

  def show(name: String) = Action{
    val title = URLDecoder.decode(name, "UTF-8")
    Media.showByTitle(title) match {
      case Nil  => NotFound
      case list => Ok(Json.toJson(list))
    }
  }

  def consume(id:String, title:String, consume : Boolean) = Action{
    Media.consume(id, title, consume)
    Ok(consume.toString)
  }


  def search(searchtext: String) = Action{
    val searchResult = Media.allTitlesWithCount  filter{ case TVShow(title,_) => title.toLowerCase.contains(searchtext.toLowerCase) }

    searchResult match {
      case Nil  => NotFound
      case list => Ok(Json.toJson(list))

    }
  }

  def delete(title: String) = Action{
    Media.delete(title)
    Ok
  }



   def update(showId: String) = Action{
     Logger.info(s"updating ${showId}")
     Import.importShowIntoDb(showId)

     Media.showByShowId(showId) match {
       case Nil  => NotFound
       case list => Ok(Json.toJson(list))
     }
   }

   def consumeAll = TODO
   def consume = TODO
   def consumeInfo = TODO
   def epGuideData = TODO

   private def lazyInitTitleCache = {
     val maybeShows = Cache.getAs[List[TVShow]](titleKeys)

     maybeShows match {
       case Some(shows)  => shows
       case None         => {
         val list= Media.allTitlesWithCount
         Cache.set(titleKeys, list)
         list
       }
     }
   }

 }
