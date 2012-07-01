package models

/**
 *
 * @author knm
 */

case class TVRageShowInfo(id: String, name: String, url: String, premiered: String,
                          started: String, ended: String, latest: String,
                          country: String, status: String, classification: String,
                          genres: String, network: String, runtime: String) {
  override def toString() = {
    """
      Premiered: %s
      |Started: %s
      |Ended: %s
      |Latest: %s
      |Country: %s
      |Genres: %s
      |Network: %s
      |Runtime (min): %s
    """.format(premiered, started, ended, latest, country, genres, network, runtime)
    .stripMargin
  }

  def toHtmlString() = {
    """
      Premiered: %s
      |Started: %s
      |Ended: %s
      |Latest: %s
      |Country: %s
      |Genres: %s
      |Network: %s
      |Runtime (min): %s
    """.format(premiered, started, ended, latest, country, genres, network, runtime)
    .replace("|", "<br />")
  }
}
