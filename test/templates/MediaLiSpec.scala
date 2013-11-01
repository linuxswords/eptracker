package templates

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models.Media
import org.joda.time.DateTime


/**
 *
 * @author knm
 */

class MediaLiSpec  extends Specification{


  "the li template " should {

    val media = new Media(DateTime.now(),Some("author"), "Game of Thrones", Some("Winter is coming"), "s01e01", None,false, "GameOfThrones")
    val html = views.html.templates.mediaAsLi(media)


    "be html" in {
      contentType(html) must equalTo("text/html")
    }

    "have a link" in {
      contentAsString(html) must contain("""<a href="/show/""" + media.title)
    }

    "provide a span date" in {
      contentAsString(html) must contain("""class="date"""")
    }
    "provide a torrent" in {
      contentAsString(html) must contain("""<span class="sprite torrent" rel="s01e01">""")
    }

  }

}
