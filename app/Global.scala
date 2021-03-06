import java.io.{FileOutputStream, File}
import org.jsoup.Jsoup
import play.api.cache.Cache
import play.api.libs.json.Json
import play.api.{Logger}
import play.{Application, GlobalSettings}
import scala.util.Try

/**
 *
 * @author knm
 */

class Global extends GlobalSettings
{
  override def onStart(app: Application)
  {
    super.onStart(app)
    import play.api.Play.current
    Logger("application").info("importing allshows.txt from epguide")
    // allshows.txt download at http://epguides.com/common/allshows.txt
    val source = Try(io.Source.fromURL("http://epguides.com/common/allshows.txt", "ISO-8859-1"))
      .getOrElse(io.Source.fromFile("allshows.txt", "ISO-8859-1"))
    val lines = source.mkString.split("\n").toList.drop(1).map{_.replace("\"","")}.map {_.replace("'", "")}
    val epGuideKeys = lines.filter(_.contains(",")).map { word =>
      val data = word.split(",")
      String.format("{ \"label\": \"%s\", \"value\": \"%s\" }",data(0).trim,data(1).trim)
    }
    val result: String = "[" + epGuideKeys.mkString(",") + "]"
    val out = new FileOutputStream("/tmp/epkeys.json");
    out.write(result.getBytes("utf-8"))
    out.close
    Cache.set("epguides", Json.parse(result))
  }
}
