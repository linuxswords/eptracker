package request

import play.api.data
import data.Forms._
import play.api.mvc._
import play.api.data.Form
import models.Media

/**
 * Wrapped Request
 */
case class EpisodeRequest[A](sidebarItems: (Seq[Media],Seq[Media]), importForm: Form[String], request: Request[A]) extends WrappedRequest(request)


object EpisodeRequest {

  def EpisodeAction(block: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>

      val importForm = Form(
        "epGuideId" -> nonEmptyText
      )

      def recent = Media.recent()
      def upcoming = Media.upcoming()
      val sidebarItems =  (recent, upcoming)

      val episodeRequest = new EpisodeRequest(sidebarItems, importForm, request)
      block(episodeRequest)

    }
  }

  implicit def requestToImportForm[A](request: Request[A]): Form[String] = {
    request.asInstanceOf[EpisodeRequest[A]].importForm
  }

  implicit def requestToSidebarItems[A](request: Request[A]):  (Seq[Media],Seq[Media]) = {
    request.asInstanceOf[EpisodeRequest[A]].sidebarItems
  }

}