/**
*
*   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
*   Group Licensed under a specific end user license agreement;
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package fr.liglab.adele.icasa.remote.wisdom.impl;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.clockservice.ClockListener;
import fr.liglab.adele.icasa.listener.MultiEventListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.remote.wisdom.RemoteEventBroadcast;
import fr.liglab.adele.icasa.remote.wisdom.util.IcasaJSONUtil;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.Controller;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.*;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.websockets.Publisher;

import java.util.Date;
import java.util.UUID;

@Component
@Provides(specifications = {RemoteEventBroadcast.class, Controller.class})
@Instantiate
public class EventBroadcast extends DefaultController implements RemoteEventBroadcast {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG_REMOTE+".event");

    @Property (name = "url", value="/icasa/websocket/event")
	private String url;

	@Requires
	private Publisher publisher;

	@Requires
	private ContextManager _ctxMgr;



	@Requires
	private Clock _clock;

	private ICasaEventListener _iCasaListener;

	private ClockEventListener _clockListener;



	@Validate
	protected void start() {
        logger.debug("start");
		// Register iCasa listeners
		_iCasaListener = new ICasaEventListener();
		_ctxMgr.addListener(_iCasaListener);


		_clockListener = new ClockEventListener();
		_clock.addListener(_clockListener);

	}

	@Invalidate
	protected void stop() {
        logger.debug("stop");
		// Unregister iCasa listeners

		if (_iCasaListener != null) {
			_ctxMgr.removeListener(_iCasaListener);
			_iCasaListener = null;
		}
		if (_clockListener != null) {
			_clock.removeListener(_clockListener);
			_clockListener = null;
		}

	}


	private UUID _lastEventId = UUID.randomUUID();

	private String generateUUID() {
		_lastEventId = UUID.randomUUID();
		return _lastEventId.toString();
	}

    @Opened("{name}")
    public void open(@Parameter("name") String name) {
        System.out.println("Web socket opened => " + name);
    }

    @Closed("{name}")
    public void close(@Parameter("name") String name) {
        System.out.println("Web socket closed => " + name);
    }

    @Route(method = HttpMethod.GET, uri = "/icasa/websocket/event")
    public Result handshake() {
        return ok();
    }

    @OnMessage("{name}")
    public void onMessage(@Parameter("name") String name) {
        System.out.println("Receiving message on " + name + " : ");
        publisher.publish(url, "OK");
    }

	public void sendEvent(String eventType, JSONObject event) {
        logger.debug("!! sending event : " + eventType  + "to " + url);
		try {
			event.put("eventType", eventType);
			event.put("id", generateUUID());
			event.put("time", new Date().getTime());
            publisher.publish(url, event.toString());
		} catch (JSONException e) {
			e.printStackTrace();
            logger.error("Building message error" + eventType, e);
		}
	}

	private class ICasaEventListener implements MultiEventListener {

		@Override
		public void deviceTypeRemoved(String deviceType) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceTypeId", deviceType);
				sendEvent("device-type-removed",json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
                e.printStackTrace();
			}
		}

		@Override
		public void deviceTypeAdded(String deviceType) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceTypeId", deviceType);
				sendEvent("device-type-added", json);
			} catch (JSONException e) {
				e.printStackTrace();
                logger.error("Building message error" + json, e);
			}
		}

		@Override
		public void deviceMoved(LocatedDevice device, Position oldPosition, Position newPosition) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceId", device.getSerialNumber());
				json.put("device", IcasaJSONUtil.getDeviceJSON(device, _ctxMgr));
				sendEvent("device-position-update", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void deviceAdded(LocatedDevice device) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceId", device.getSerialNumber());
				json.put("device", IcasaJSONUtil.getDeviceJSON(device, _ctxMgr));
				sendEvent("device-added", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void deviceRemoved(LocatedDevice device) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceId", device.getSerialNumber());
				sendEvent("device-removed", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue, Object newValue) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceId", device.getSerialNumber());
				json.put("device", IcasaJSONUtil.getDeviceJSON(device, _ctxMgr));
				sendEvent("device-property-updated", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void devicePropertyAdded(LocatedDevice device, String propertyName) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceId", device.getSerialNumber());
				json.put("device", IcasaJSONUtil.getDeviceJSON(device, _ctxMgr));
				sendEvent("device-property-added", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
			JSONObject json = new JSONObject();
			try {
				json.put("deviceId", device.getSerialNumber());
				json.put("device", IcasaJSONUtil.getDeviceJSON(device, _ctxMgr));
				sendEvent("device-property-removed", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

        /**
         * Invoked when a device has been attached to another device
         *
         * @param container
         * @param child
         */
        public void deviceAttached(LocatedDevice container, LocatedDevice child) {
            JSONObject json = new JSONObject();
            try {
                json.put("deviceId", container.getSerialNumber());
                json.put("container", IcasaJSONUtil.getDeviceJSON(container, _ctxMgr));
                json.put("child", IcasaJSONUtil.getDeviceJSON(child, _ctxMgr));
                sendEvent("device-attached-device", json);
            } catch (JSONException e) {
                logger.error("Building message error" + json, e);
                e.printStackTrace();
            }
        }

        /**
         * * Invoked when a device has been detached from another device
         *
         * @param container
         * @param child
         */
        public void deviceDetached(LocatedDevice container, LocatedDevice child) {
            JSONObject json = new JSONObject();
            try {
                json.put("deviceId", container.getSerialNumber());
                json.put("container", IcasaJSONUtil.getDeviceJSON(container, _ctxMgr));
                json.put("child", IcasaJSONUtil.getDeviceJSON(child, _ctxMgr));
                sendEvent("device-detached-device", json);
            } catch (JSONException e) {
                logger.error("Building message error" + json, e);
                e.printStackTrace();
            }
        }

        /**
         * Callback notifying when the device want to trigger an event.
         *
         * @param device the device triggering the event.
         * @param data   the content of the event.
         */
        @Override
        public void deviceEvent(LocatedDevice device, Object data) {
            JSONObject json = new JSONObject();
            try {
                json.put("deviceId", device.getSerialNumber());
                json.put("device", IcasaJSONUtil.getDeviceJSON(device, _ctxMgr));
                json.put("event-data", String.valueOf(data));
                sendEvent("device-event", json);
            } catch (JSONException e) {
                logger.error("Building message error" + json, e);
                e.printStackTrace();
            }
        }


        @Override
		public void zoneVariableAdded(Zone zone, String variableName) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-variable-added", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void zoneVariableRemoved(Zone zone, String variableName) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
			JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-variable-removed", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void zoneVariableModified(Zone zone, String variableName, Object oldValue, Object newValue) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
			JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-variable-updated", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void zoneMoved(Zone zone, Position oldPosition, Position newPosition) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-moved", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void zoneResized(Zone zone) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-resized", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void zoneParentModified(Zone zone, Zone oldParent, Zone newParent) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-parent-updated", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

        /**
         * Invoked when a device has been attached a zone
         *
         * @param container
         * @param child
         */
        public void deviceAttached(Zone container, LocatedDevice child) {
            if(isAScopeZone(container)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
            try {
                json.put("zoneId", container.getId());
                json.put("zone", IcasaJSONUtil.getZoneJSON(container));
                json.put("device", IcasaJSONUtil.getDeviceJSON(child, _ctxMgr));
                sendEvent("device-attached-zone", json);
            } catch (JSONException e) {
                logger.error("Building message error" + json, e);
                e.printStackTrace();
            }
        }

        /**
         * * Invoked when a device has been detached from a zone
         *
         * @param container
         * @param child
         */
        public void deviceDetached(Zone container, LocatedDevice child) {
            if(isAScopeZone(container)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
            try {
                json.put("zoneId", container.getId());
                json.put("zone", IcasaJSONUtil.getZoneJSON(container));
                json.put("device", IcasaJSONUtil.getDeviceJSON(child, _ctxMgr));
                sendEvent("device-detached-zone", json);
            } catch (JSONException e) {
                logger.error("Building message error" + json, e);
                e.printStackTrace();
            }
        }

        @Override
		public void zoneAdded(Zone zone) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				json.put("zone", IcasaJSONUtil.getZoneJSON(zone));
				sendEvent("zone-added", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

		@Override
		public void zoneRemoved(Zone zone) {
            if(isAScopeZone(zone)) {
                return ; // we don't send scope zones to the frontend
            }
            JSONObject json = new JSONObject();
			try {
				json.put("zoneId", zone.getId());
				sendEvent("zone-removed", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

        private boolean isAScopeZone(Zone zone){
            //turnaround: As scope does not exist, zones
            //containing #zone in the name are considered as scope zones, they are used in medical devices
            //to identify in simulation when a simulated person use the medical device.
            if (zone.getId().contains("#zone")) {
                return true;
            }
            return false;
        }


	}

	private class ClockEventListener implements ClockListener {

		@Override
		public void factorModified(int oldFactor) {
			sendClockModifiedEvent();
		}

		@Override
		public void startDateModified(long oldStartDate) {
			sendClockModifiedEvent();
		}

		@Override
		public void clockPaused() {
			sendClockModifiedEvent();
		}

		@Override
		public void clockResumed() {
			sendClockModifiedEvent();
		}

		@Override
		public void clockReset() {
			sendClockModifiedEvent();
		}

		private void sendClockModifiedEvent() {
			JSONObject json = new JSONObject();
			try {
				json.put("clock", IcasaJSONUtil.getClockJSON(_clock));
				sendEvent("clock-modified", json);
			} catch (JSONException e) {
                logger.error("Building message error" + json, e);
				e.printStackTrace();
			}
		}

	}


}
