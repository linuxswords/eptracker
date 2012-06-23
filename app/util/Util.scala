package util

import java.net.{HttpURLConnection, URL}
import controllers.routes
import java.io._
import play.api.Logger


/**
 *
 * @author knm
 */

object Util
{

  def saveFileFromUrl(file: File, url: String):Unit =
  {

    var out: OutputStream = null
    var in: InputStream = null

    Logger("application").debug("getting cast jpg from " + url)


    try
    {
      var jurl = new URL(url)
      val uc = jurl.openConnection()
      val connection = uc.asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      val buffer: Array[Byte] = new Array[Byte](1024)
      var numRead: Int = 0
      in = connection.getInputStream()
      out = new BufferedOutputStream(new FileOutputStream(file))
      while( { numRead = in.read(buffer); numRead != -1})
      {
        out.write(buffer, 0, numRead);
      }
    }
    catch
      {
        case e: Exception => println(e.printStackTrace())
      }
    finally {
      // retry if cast.jpg is not available
      if(out == null && url.contains("cast.jpg"))
      {
        val newurl = url.replace("cast.jpg", "logo.jpg")
        saveFileFromUrl(file, newurl)
      }
      else if(out != null)
      {
        out.close()
        in.close()
      }
    }

  }
}
