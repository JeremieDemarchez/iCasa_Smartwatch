/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.remote.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.handler.OnMessage;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.barjo.atmosgi.AtmosphereService;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import fr.liglab.adele.icasa.simulator.LocatedDevice;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.Zone;
import fr.liglab.adele.icasa.simulator.listener.MultiEventListener;

@Component(name = "iCasa-event-broadcast")
@Instantiate(name = "iCasa-event-broadcast-1")
public class EventBroadcast extends OnMessage<String> {

	@Property(name = "mapping", value = "/event")
	private String mapping;

	private final List<AtmosphereInterceptor> _interceptors = new ArrayList<AtmosphereInterceptor>();

	@Requires
	private AtmosphereService _atmoService;

	@Requires
	private HttpService _httpService;

	@Requires
	private SimulationManager _simulMgr;

	
	// TODO: Is it really necessary?
	@Requires
	DeviceREST deviceREST;
	
	@Requires
	PersonREST personREST;
	
	@Requires
	ZoneREST zoneREST;
	
	
	
	private Broadcaster _eventBroadcaster;

	private ICasaEventListener _iCasaListener;

	private final BundleContext _context;
	
	

	public EventBroadcast(BundleContext context) {
		_context = context;
	}

	@Validate
	private void start() {
		_iCasaListener = new ICasaEventListener();
		_simulMgr.addListener(_iCasaListener);

		_eventBroadcaster = _atmoService.getBroadcasterFactory().get();

		// Register the server (itself)
		_interceptors.add(new AtmosphereResourceLifecycleInterceptor());
		_atmoService.addAtmosphereHandler(mapping, this, _eventBroadcaster, _interceptors);

		// Register the web client
		try {
			_httpService.registerResources("/event", "/web", null);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
	}

	@Invalidate
	private void stop() {
		if (_iCasaListener != null) {
			_simulMgr.removeListener(_iCasaListener);
			_iCasaListener = null;
		}

		_atmoService.removeAtmosphereHandler(mapping);
		_interceptors.clear();

		_httpService.unregister("/event");
	}

	@Override
	public void onMessage(AtmosphereResponse atmosphereResponse, String s) throws IOException {
		atmosphereResponse.getWriter().write(s);
	}

	private UUID _lastEventId = UUID.randomUUID();

	private String generateUUID() {
		_lastEventId = UUID.randomUUID();
		return _lastEventId.toString();
	}

	private void sendEvent(JSONObject event) {
		try {
			event.put("id", generateUUID());
			event.put("time", new Date().getTime());
			_eventBroadcaster.broadcast(event.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class ICasaEventListener implements MultiEventListener {

        @Override
        public void deviceTypeRemoved(String deviceType) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-type-removed");
                json.put("deviceTypeId", deviceType);
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deviceTypeAdded(String deviceType) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-type-added");
                json.put("deviceTypeId", deviceType);                
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
		public void deviceMoved(LocatedDevice device, Position position) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "device-position-update");
				json.put("deviceId", device.getSerialNumber());
				json.put("device", deviceREST.getDeviceJSON(device));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void deviceAdded(LocatedDevice device) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "device-added");
				json.put("deviceId", device.getSerialNumber());
				json.put("device", deviceREST.getDeviceJSON(device));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void deviceRemoved(LocatedDevice device) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "device-removed");
				json.put("deviceId", device.getSerialNumber());
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

        @Override
        public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-property-updated");
                json.put("deviceId", device.getSerialNumber());
    				json.put("device", deviceREST.getDeviceJSON(device));
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void devicePropertyAdded(LocatedDevice device, String propertyName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-property-added");
                json.put("deviceId", device.getSerialNumber());
                //json.put("propertyName", propertyName);
                json.put("device", deviceREST.getDeviceJSON(device));
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "device-property-removed");
                json.put("deviceId", device.getSerialNumber());
                //json.put("propertyName", propertyName);
                json.put("device", deviceREST.getDeviceJSON(device));
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

		@Override
		public void personMoved(Person person, Position position) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "person-position-update");
				json.put("personId", person.getName());
				// New position is maybe enough
				json.put("person", personREST.getPersonJSON(person));				
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void personAdded(Person person) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "person-added");
				json.put("personId", person.getName());
				json.put("person", personREST.getPersonJSON(person));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void personRemoved(Person person) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "person-removed");
				json.put("personId", person.getName());
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

        public void personDeviceAttached(Person person, LocatedDevice device) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "person-device-attached");
                json.put("personId", person.getName());
                json.put("deviceId", device.getSerialNumber());
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void personDeviceDetached(Person person, LocatedDevice device) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "person-device-dettached");
                json.put("personId", person.getName());
                json.put("deviceId", device.getSerialNumber());
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

		@Override
		public void zoneVariableAdded(Zone zone, String variableName) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "zone-variable-added");
				json.put("zoneId", zone.getId());
				//json.put("variableName", variableName);
				json.put("zone", zoneREST.getZoneJSON(zone));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void zoneVariableRemoved(Zone zone, String variableName) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "zone-variable-removed");
				json.put("zoneId", zone.getId());
				//json.put("variableName", variableName);	
				json.put("zone", zoneREST.getZoneJSON(zone));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "zone-variable-updated");
				json.put("zoneId", zone.getId());
				//json.put("variableName", variableName);
				json.put("zone", zoneREST.getZoneJSON(zone));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void zoneMoved(Zone zone, Position oldPosition) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-moved");
                json.put("zoneId", zone.getId());
    				json.put("zone", zoneREST.getZoneJSON(zone));
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
		}

		@Override
		public void zoneResized(Zone zone) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-resized");
                json.put("zoneId", zone.getId());
                json.put("zone", zoneREST.getZoneJSON(zone));
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
		}

		@Override
		public void zoneParentModified(Zone zone, Zone oldParent) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "zone-parent-updated");
                json.put("zoneId", zone.getId());
                json.put("zone", zoneREST.getZoneJSON(zone));
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
		}

		@Override
		public void zoneAdded(Zone zone) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "zone-added");
				json.put("zoneId", zone.getId());
				json.put("zone", zoneREST.getZoneJSON(zone));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void zoneRemoved(Zone zone) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "zone-removed");
				json.put("zoneId", zone.getId());
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
      public void personTypeAdded(String personType) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "person-type-removed");
                json.put("personTypeId", personType);
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
      }

		@Override
      public void personTypeRemoved(String personType) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventType", "person-type-added");
                json.put("personTypeId", personType);
                sendEvent(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
      }

	}

}
