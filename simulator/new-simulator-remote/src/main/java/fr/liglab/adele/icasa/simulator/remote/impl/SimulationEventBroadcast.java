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
package fr.liglab.adele.icasa.simulator.remote.impl;

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

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.clock.ClockListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;
import fr.liglab.adele.icasa.simulator.listener.PersonTypeListener;
import fr.liglab.adele.icasa.simulator.listener.SimulatedLocatedDeviceListener;
import fr.liglab.adele.icasa.simulator.remote.impl.util.IcasaSimulatorJSONUtil;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutorListener;

@Component(name = "iCasa-simulation-event-broadcast")
@Instantiate(name = "iCasa-simulation-event-broadcast-1")
public class SimulationEventBroadcast extends OnMessage<String> {

	@Property(name = "mapping", value = "/event")
	private String mapping;

	private final List<AtmosphereInterceptor> _interceptors = new ArrayList<AtmosphereInterceptor>();

	@Requires
	private AtmosphereService _atmoService;

	@Requires
	private HttpService _httpService;

	@Requires
	private SimulationManager _simulMgr;
	
	@Requires
	private ScriptExecutor _scriptExecutor;
	

	@Requires
	private Clock _clock;

	private Broadcaster _eventBroadcaster;

	private ICasaSimulatedEventListener _iCasaListener;

	private ScriptPlayerEventListener _scriptListener;

	// private final BundleContext _context;

	public SimulationEventBroadcast(BundleContext context) {
		// _context = context;
	}

	@Validate
	protected void start() {

		// Register iCasa listeners
		_iCasaListener = new ICasaSimulatedEventListener();
		_simulMgr.addListener(_iCasaListener);
		
		
		_scriptListener = new ScriptPlayerEventListener();
		_scriptExecutor.addListener(_scriptListener);

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
	protected void stop() {

		// Unregister iCasa listeners

		if (_iCasaListener != null) {
			_simulMgr.removeListener(_iCasaListener);
			_iCasaListener = null;
		}
		
		if (_scriptListener != null) {
			_scriptExecutor.removeListener(_scriptListener);
			_scriptListener = null;
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

	private class ICasaSimulatedEventListener implements PersonListener, PersonTypeListener, SimulatedLocatedDeviceListener {



		@Override
		public void deviceMoved(LocatedDevice device, Position position) {
			//Handled by EventBroadcast in icasa.remote
		}

		@Override
		public void deviceAdded(LocatedDevice device) {
			//Handled by EventBroadcast in icasa.remote
		}

		@Override
		public void deviceRemoved(LocatedDevice device) {
			//Handled by EventBroadcast in icasa.remote
		}

		@Override
		public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue) {
			//Handled by EventBroadcast in icasa.remote
		}

		@Override
		public void devicePropertyAdded(LocatedDevice device, String propertyName) {
			//Handled by EventBroadcast in icasa.remote
		}

		@Override
		public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
			//Handled by EventBroadcast in icasa.remote
		}

		@Override
		public void personMoved(Person person, Position position) {
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", "person-position-update");
				json.put("personId", person.getName());
				// New position is maybe enough
				json.put("person", IcasaSimulatorJSONUtil.getPersonJSON(person));
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
				json.put("person", IcasaSimulatorJSONUtil.getPersonJSON(person));
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
	
	private class ScriptPlayerEventListener implements ScriptExecutorListener {

		@Override
      public void scriptPaused(String scriptName) {
			sendModifiedEvent("script-paused", scriptName);	
      }

		@Override
      public void scriptResumed(String scriptName) {
			sendModifiedEvent("script-resumed", scriptName);	
      }

		@Override
      public void scriptStopped(String scriptName) {
			sendModifiedEvent("script-stopped", scriptName);		      
      }

		@Override
      public void scriptStarted(String scriptName) {
			sendModifiedEvent("script-started", scriptName);	      
      }
		
		private void sendModifiedEvent(String eventType, String scriptName) {
			System.out.println("Sending ----> " + eventType);
			JSONObject json = new JSONObject();
			try {
				json.put("eventType", eventType);
				json.put("script", IcasaSimulatorJSONUtil.getScriptJSON(scriptName, _scriptExecutor));
				sendEvent(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

}
