package util


import org.specs2.mutable._
import org.specs2.mutable.Specification

/**
 *
 * @author knm
 */

class TVRageSpec extends Specification {

  "TVRage show info "  should  {

    "for Alias must return an info object" in {

      val info = TVRageUtil.showInfo("Alias")

      info must beSome

    }

    "for Alias must be filled correctly" in {

      // this is the answer we expect (newlines included)

//      <pre>Show ID@2537
//      Show Name@Alias
//      Show URL@http://www.tvrage.com/Alias
//      Premiered@2001
//      Started@Sep/30/2001
//      Ended@May/22/2006
//      Latest Episode@05x17^All the Time in the World^May/22/2006
//      Country@USA
//      Status@Canceled/Ended
//      Classification@Scripted
//      Genres@Action | Adventure | Drama
//      Network@ABC
//      Runtime@60


      val info = TVRageUtil.showInfo("Alias").get

      info.id must equalTo("2537")
      info.name must equalTo("Alias")
      info.url must equalTo("http://www.tvrage.com/Alias")
      info.premiered must equalTo("2001")
      info.started must equalTo("Sep/30/2001")
      info.ended must equalTo("May/22/2006")
      info.latest must equalTo("05x17^All the Time in the World^May/22/2006")
      info.country must equalTo("USA")
      info.status must equalTo("Canceled/Ended")
      info.classification must equalTo("Scripted")
      info.genres must equalTo("Action | Adventure | Drama")
      info.network must equalTo("ABC")
      info.runtime must equalTo("60")

    }



  }

}
