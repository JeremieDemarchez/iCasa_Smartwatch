package models

import play.api.libs.json.JsValue
import play.api.libs.json.Json._
import scala.Some
import play.api.libs.EventSource

// Define a generic event
trait Event {
  def event: String

  // event is a specific Server sent event attribute
  def data: String
}

case class DeviceEvent(data: String) extends Event {
  override def event = "device-event"
}

// Defines the Enumerator for various kind of ZapEvent
object DeviceStreams {

  import scala.util.Random

  import play.api.libs.iteratee._
  import play.api.libs.concurrent._

  // Please note that in Play 2.1.x fromCallback will be rename to generateM
  val deviceStream: Enumerator[DeviceEvent] = Enumerator.fromCallback[DeviceEvent] {
    () => Promise.timeout(Some(DeviceEvent("Device " + Random.nextInt(50) + 100)), Random.nextInt(3000))
  }

  // Adapter from a stream of DeviceEvent to a stream of JsValue, to generate Json content.
  // event is a specific keywork in the Server sent events specification.
  // See also http://dev.w3.org/html5/eventsource/
  val asJson: Enumeratee[DeviceEvent, JsValue] = Enumeratee.map[DeviceEvent] {
    deviceEvent =>
      play.Logger.info("asJson> " + deviceEvent)
      toJson(Map("event" -> toJson(deviceEvent.event), "data" -> toJson(deviceEvent.data)))
  }

//  val events: Enumerator[DeviceEvent] = {
//    deviceStream
//  }

}
