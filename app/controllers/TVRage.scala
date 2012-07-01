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
    Ok(info.get.status)
  }

}
