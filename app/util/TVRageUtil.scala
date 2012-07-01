package util

import io.Source
import java.net.{URLEncoder, URL}

/**
 *
 * @author knm
 */

case class TVRageShowInfo(id:String, name:String, url:String, premiered:String,
                     started:String, ended:String, latest:String,
                     country:String, status:String, classification:String,
                     genres:String, network:String, runtime:String)

object TVRageUtil {

  val url = "http://services.tvrage.com/tools/quickinfo.php?"

  // must match

//  <pre>Show ID@2537
//  Show Name@Alias
//  Show URL@http://www.tvrage.com/Alias
//  Premiered@2001
//  Started@Sep/30/2001
//  Ended@May/22/2006
//  Latest Episode@05x17^All the Time in the World^May/22/2006
//  Country@USA
//  Status@Canceled/Ended
//  Classification@Scripted
//  Genres@Action | Adventure | Drama
//  Network@ABC
//  Runtime@60

  val urlMatch = "<pre>Show ID@(.*)\nShow Name@(.*)\nShow URL@(.*)\nPremiered@(.*)\nStarted@(.*)\nEnded@(.*)\nLatest Episode@(.*)\nCountry@(.*)\nStatus@(.*)\nClassification@(.*)\nGenres@(.*)\nNetwork@(.*)\nRuntime@(.*)\n".r
//  val urlMatch = "(?s)<pre>Show ID@(.*?)Show Name@(.*)Show URL@(.*)Premiered@(.*)Started@(.*)Ended@(.*)Latest Episode@(.*)Country@(.*)Status@(.*)Classification@(.*)Genres@(.*)Network@(.*)Airtime@(.*)Runtime@(.*)".r


  def showInfo(showName: String) : Option[TVRageShowInfo]= {
    val encodedName = URLEncoder.encode(showName, "UTF-8")
    val html = Source.fromURL(url + "show=" + encodedName).mkString
    html match {
      case urlMatch(id, name, url, premiered,
                           started, ended, latest,
                           country, status, classification,
                           genres, network, runtime) =>
        val info = new TVRageShowInfo(id, name, url, premiered,
                                                      started, ended, latest,
                                                      country, status, classification,
                                                      genres, network, runtime)
        Option(info)
      case _ => None
    }
  }


}
