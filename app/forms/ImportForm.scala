package forms

import play.api.data.Form

import play.api.data
import data.Forms._
import play.api.data.Form
/**
 * @author knm
 */
object ImportForm {

  def importForm = Form(
    "epGuideId" -> nonEmptyText
  )
}
