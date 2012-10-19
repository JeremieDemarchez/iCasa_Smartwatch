package controllers

import play.api._
import libs.EventSource
import libs.json.Json._
import play.api.mvc._
import models.DeviceStreams._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def streamDeviceEvents() = Action {
    Ok.feed(deviceStream.through(asJson.compose(EventSource()))).as("text/event-stream")
  }
  
}