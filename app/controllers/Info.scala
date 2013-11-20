package controllers

import forms.SearchForm
import models.{TVShow, Media}
import play.api.cache.Cache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Controller
import request.EpisodeRequest.EpisodeAction
import java.net.URLDecoder
import play.api.Play.current

import controllers.Search.titleKeys
import scala.util.Random

/**
 *
 * @author knm
 */


object Info extends Controller
{

  def consumeInfo = EpisodeAction{ implicit request =>
    val showMap = Media.consumeInfoAll
    Ok(Json.toJson(showMap))
  }

}
