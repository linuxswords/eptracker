package controllers

import play.api.mvc.{Action, Controller}
import org.jsoup.Jsoup
import play.api.db.DB
import anorm._
import play.api.Play.current
import models.{MediaEntity, TVShowEntity, Media}
import controllers.Show._
import java.text.MessageFormat
import scala.Predef._
import play.api.{Play, Logger}
import java.io.File
import com.googlecode.mapperdao.utils.Setup
import org.joda.time.format.DateTimeFormat

/**
 *
 * @author knm
 */

object Import extends Controller
{
  val dataSource = play.db.DB.getDataSource("default")
  val(jdbc, mapperDao, queryDao, transcationManager) = Setup.mysql(dataSource, List(MediaEntity, TVShowEntity))
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

  def importTheShow() = Action { implicit request =>
    Show.importForm.bindFromRequest().fold(
      errorForm =>   {
        val recent = Media.recent()
        val upcoming = Media.upcoming()
        implicit val sidebarItems =  (recent, upcoming)
        BadRequest(views.html.shows(Media.shows(1, 10, 1), 1, errorForm))
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

  def formatSql(nr:String, id:String, date:String, subtitle:String, title: String, epGuideId:String) =   {
    val ctitle = title.replace("'", "''")
    val stitle = subtitle.replace("'", "''").replace("[Recap]", "").replace("[Trailer]", "")
    ("insert into Media(consumed,title,subtitle,identifier,publishingDate,showid) select 0,'%s','%s','%s','%s', '%s' from ddual "
      .format(ctitle.trim, stitle.trim, convertId(id), convertDate(date), epGuideId) +
      " where not exists (select * from Media where upper(showid) = '%s' and identifier = '%s' and subtitle = '%s');")
      .format(epGuideId.toUpperCase, convertId(id), stitle.trim)
  }


  def processLine(s: String, title: String, epGuideId: String) =
  {
    s match {
      case nrIdDateTitle(nr,id,date,subtitle) =>
        mapperDao.insert(MediaEntity, new Media(convertDate(date),None, title, Some(subtitle), Option(id), None, false, Option(title)))
      // formatSql(nr, id, date, subtitle, title, epGuideId)
      case nrIdProdDateTitle(nr,id,_,date,subtitle) =>
        mapperDao.insert(MediaEntity, new Media(convertDate(date),None, title, Some(subtitle), Option(id), None, false, Option(title)))
        //formatSql(nr, id, date, subtitle, title, epGuideId)
      case _ => ""
    }
  }

  def importShowIntoDb(epGuideId: String) = {

    val html = Jsoup.connect("http://epguides.com/" + epGuideId).get
    val texts = html.select("div#eplist pre").text.split("\n").toList.par // parallel computation here!
    val title = html.select("h1").text

      // image
    val url = "http://epguides.com/" + epGuideId + "/cast.jpg"
    val file: File = Play.application.getFile("public/images/show/" + epGuideId + "-cast.jpg")
    util.Util.saveFileFromUrl(file, url)


    val sql = for{
      line <- texts
    } {
      processLine(line, title, epGuideId)
    }

//    println(sql.mkString)
//    DB.withConnection{ implicit connection =>
//      SQL(sql.mkString).execute()
//    }
    title
   }
}
