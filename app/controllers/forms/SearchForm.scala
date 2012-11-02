package controllers.forms

import play.api.data
import data.Form
import data.Forms._

/**
 *
 * @author knm
 */
trait SearchForm {
  implicit val searchForm = Form[SearchText](
    mapping(
    "text" -> nonEmptyText
    )(SearchText.apply)(SearchText.unapply)
    )
}

case class SearchText(text: String)
