package controllers

import play.api.mvc.{Action, Controller}
import util.TVRageUtil

/**
 *
 * @author knm
 */

object TVRage extends Controller{

  def info(name:String) = Action {
    val info = TVRageUtil.showInfo(name)
    val result = info match
    {
      case Some(info) => info.toHtmlString()
      case None => "coult not get tvrage info :("
    }
    Ok(result)
  }

}
