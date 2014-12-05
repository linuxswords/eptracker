package models

/**
 * @author knm
 */
case class ShowStat(showId: String, consumeStatus: String, size: Int)

object  ShowStat{
  implicit val showStatFormats = play.api.libs.json.Json.format[ShowStat]
}
