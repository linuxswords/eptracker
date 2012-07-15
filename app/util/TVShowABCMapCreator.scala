package util

import models.TVShow
import collection.immutable.TreeMap
import collection.SortedMap

/**
 *
 * @author knm
 */

object TVShowABCMapCreator {

  def createMap(shows: List[TVShow]) :Map[String, List[TVShow]]= {
    shows.groupBy( _.title.toUpperCase().substring(0,1) )
  }

}
