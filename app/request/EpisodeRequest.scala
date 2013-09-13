package request

import play.api.data
import data.Forms._
import play.api.mvc._
import play.api.data.Form
import models.Media

/**
 * Wrapped Request
 */
case class EpisodeRequest[A](sidebarItems: (Seq[Media],Seq[Media]), request: Request[A]) extends WrappedRequest(request)


object EpisodeRequest {

  def EpisodeAction(block: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>

      def recent = Media.recent()
      def upcoming = Media.upcoming()
      val sidebarItems =  (recent, upcoming)

      val episodeRequest = new EpisodeRequest(sidebarItems, request)
      block(episodeRequest)

    }
  }

  implicit def requestToSidebarItems[A](request: Request[A]):  (Seq[Media],Seq[Media]) = {
    request.asInstanceOf[EpisodeRequest[A]].sidebarItems
  }

}