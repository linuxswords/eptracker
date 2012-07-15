package util

import org.specs2.mutable.Specification
import models.TVShow

/**
 *
 * @author knm
 */

class ABCMapCreatorSpec extends Specification {

  "ABCMapCreator" should {


    val alias = TVShow("Alias", 0)
    val andromeda = TVShow("andromeda", 0)
    val babylon = TVShow("Babylon", 0)


    "for two shows return a map with two entries" in {

      val map = TVShowABCMapCreator.createMap(List(alias, babylon))
      map.size must equalTo(2)
    }

    "for three shows with two of the same start character ignoring case" in {
      val map = TVShowABCMapCreator.createMap(List(alias, babylon, andromeda))

      map.size must equalTo(2)
      map("A").size must equalTo(2)
    }
  }
}
