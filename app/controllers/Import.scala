package controllers

import forms.SearchForm
import request.EpisodeRequest._
import play.api.mvc.{Action, Controller}
import org.jsoup.Jsoup
import play.api.Play.current
import models.{MediaEntity, TVShowEntity, Media}
import scala.Predef._
import play.api.Play
import java.io.File
import com.googlecode.mapperdao.utils.Setup
import org.joda.time.format.DateTimeFormat

import com.googlecode.mapperdao.Query._
import com.googlecode.mapperdao.jdbc.Transaction
import com.googlecode.mapperdao.jdbc.Transaction.{Isolation, Propagation}

/**
 *
 * @author knm
 */

object Import extends Controller with SearchForm
{
  val dataSource = play.api.db.DB.getDataSource("default")
  val(jdbc, mapperDao, queryDao, transcationManager) = Setup.mysql(dataSource, List(MediaEntity, TVShowEntity))
  val me = MediaEntity
  val fmt = DateTimeFormat.forPattern("yyyy-MM-dd")

  val nrIdDateTitle = "(^[0-9]+)\\s*([0-9-]+) {8,}([0-9]{2}/[a-zA-Z]{3}/[0-9]{2})\\s*(.*)".r
  val nrIdProdDateTitle = "(^[0-9]+)\\s*([0-9-]+)\\s+[0-9/a-zA-Z]+\\s*(([0-9]{2}/[a-zA-Z]{3}/[0-9]{2}))\\s{3}(.*)".r
  val dateMatch = """([0-9]{2})/([a-zA-Z]{3})/([0-9]{2})""".r
  val idMatch = """([0-9]+)-([0-9]+)""".r
  val monthMap = Map(
        "Jan" -> "01",
        "Feb" -> "02",
        "Mar" -> "03",
        "Apr" -> "04",
        "May" -> "05",
        "Jun" -> "06",
        "Jul" -> "07",
        "Aug" -> "08",
        "Sep" -> "09",
        "Oct" -> "10",
        "Nov" -> "11",
        "Dec" -> "12"
  )

  def importTheShow() = EpisodeAction { implicit request =>
    val importForm = requestToImportForm(request)
    importForm.bindFromRequest().fold(
      errorForm =>   {
        val recent = Media.recent()
        val upcoming = Media.upcoming()
        implicit val sidebarItems =  (recent, upcoming)
        BadRequest(views.html.shows(Media.shows(1, 10, 1), 1))
      },

      id => Redirect(routes.Import.importShow(id))
    )
  }

  def importShow(epguideId:String) = Action{

    val title = importShowIntoDb(epguideId)
    Redirect(routes.Show.show(title))
  }

  def convertDate(s: String) = {
    s match {
      case dateMatch(day,month,year) =>
        val fourDigitYear = if(year.toInt > 50) "19" + year else "20" + year
        fmt.parseDateTime(fourDigitYear + "-" + monthMap(month) + "-" +day)
    }
  }

  def convertId(s: String) = {
    s match {
      case idMatch(season, ep) =>
        val twoDigitSeason = if(season.toInt < 10) "0" + season else season
        "s" + twoDigitSeason + "e" + ep
    }
  }

  def conditionalInsert(media:Media){
     val tx = Transaction.get(transcationManager, Propagation.Nested, Isolation.ReadCommited, -1)
    tx {() =>
      val result = queryDao.querySingleResult(select from me where me.title === media.title and me.identifier === media.identifier)
      result match {
        case None => {
          mapperDao.insert(me, media)
        }
        case _ =>
      }
    }
  }

  def processLine(s: String, title: String, epGuideId: String) =
  {
    s match {
      case nrIdDateTitle(nr,id,date,subtitle) =>
        conditionalInsert(new Media(convertDate(date),None, cleanString(title), Some(cleanString(subtitle)), convertId(id), None, false, epGuideId))
      case nrIdProdDateTitle(nr,id,_,date,subtitle) =>
        conditionalInsert(new Media(convertDate(date),None, cleanString(title), Some(cleanString(subtitle)), convertId(id), None, false, epGuideId))
      case _ => ""
    }
  }

  def cleanString(text:String) = {
    text.replace("[Recap]","")replace("[Trailer]","")replace("'","''")
  }

  def importShowIntoDb(epGuideId: String) = {

    val html = Jsoup.connect("http://epguides.com/" + epGuideId).get
    val texts = html.select("div#eplist pre").text.split("\n").toList.par // parallel computation here!
    val title = html.select("h1").text

      // image
    val url = "http://epguides.com/" + epGuideId + "/cast.jpg"
    val file: File = Play.application.getFile("public/images/show/" + epGuideId + "-cast.jpg")
    util.Util.saveFileFromUrl(file, url)

    for{
      line <- texts
    } {
      processLine(line, title, epGuideId)
    }

    title
   }
}
