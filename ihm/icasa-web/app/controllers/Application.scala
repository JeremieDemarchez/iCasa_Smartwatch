package controllers

import play.api._
import libs.EventSource
import libs.json.Json._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index()).withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Expose-Headers" -> "X-Cache-Date, X-Atmosphere-tracking-id",
      "Access-Control-Allow-Headers" ->"Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport",
      "Access-Control-Max-Age"-> "-1"
    )
  }
  
}