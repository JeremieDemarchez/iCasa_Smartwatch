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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.remote.RemoteEventBroadcast;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;
import fr.liglab.adele.icasa.simulator.listener.PersonTypeListener;
import fr.liglab.adele.icasa.simulator.listener.SimulatedLocatedDeviceListener;
import fr.liglab.adele.icasa.simulator.remote.util.IcasaSimulatorJSONUtil;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutorListener;

@Component(name = "iCasa-simulation-event-broadcast")
@Instantiate(name = "iCasa-simulation-event-broadcast-1")
public class SimulationEventBroadcast  {

	@Requires 
	private RemoteEventBroadcast _broadcaster;
	
	@Requires
	private SimulationManager _simulMgr;
	
	@Requires
	private ScriptExecutor _scriptExecutor;
	

	@Requires
	private Clock _clock;


	private ICasaSimulatedEventListener _iCasaListener;

	private ScriptPlayerEventListener _scriptListener;


	public SimulationEventBroadcast(BundleContext context) {
	}

	@Validate
	protected void start() {

		// Register iCasa listeners
		_iCasaListener = new ICasaSimulatedEventListener();
		_simulMgr.addListener(_iCasaListener);
		
		
		_scriptListener = new ScriptPlayerEventListener();
		_scriptExecutor.addListener(_scriptListener);


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
				json.put("personId", person.getName());
				// New position is maybe enough
				json.put("person", IcasaSimulatorJSONUtil.getPersonJSON(person));
				_broadcaster.sendEvent("person-position-update", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void personAdded(Person person) {
			JSONObject json = new JSONObject();
			try {
				json.put("personId", person.getName());
				json.put("person", IcasaSimulatorJSONUtil.getPersonJSON(person));
				_broadcaster.sendEvent("person-added", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void personRemoved(Person person) {
			JSONObject json = new JSONObject();
			try {
				json.put("personId", person.getName());
				_broadcaster.sendEvent("person-removed", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void personDeviceAttached(Person person, LocatedDevice device) {
			JSONObject json = new JSONObject();
			try {
				json.put("personId", person.getName());
				json.put("deviceId", device.getSerialNumber());
				_broadcaster.sendEvent("person-device-attached", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void personDeviceDetached(Person person, LocatedDevice device) {
			JSONObject json = new JSONObject();
			try {
				json.put("personId", person.getName());
				json.put("deviceId", device.getSerialNumber());
				_broadcaster.sendEvent("person-device-dettached", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}




		@Override
		public void personTypeAdded(String personType) {
			JSONObject json = new JSONObject();
			try {
				json.put("personTypeId", personType);
				_broadcaster.sendEvent("person-type-removed", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void personTypeRemoved(String personType) {
			JSONObject json = new JSONObject();
			try {
				json.put("personTypeId", personType);
				_broadcaster.sendEvent("person-type-added", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	
	private class ScriptPlayerEventListener implements ScriptExecutorListener {

		@Override
      public void scriptAdded(String scriptName) {
			sendModifiedEvent("script-added", scriptName);		      
      }

		@Override
      public void scriptRemoved(String scriptName) {
			sendModifiedEvent("script-removed", scriptName);		      
      }
		
		@Override
      public void scriptUpdated(String scriptName) {
			sendModifiedEvent("script-updated", scriptName);	      
      }
		
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
			JSONObject json = new JSONObject();
			try {
                json.put("scriptId", scriptName);
                json.put("script", IcasaSimulatorJSONUtil.getScriptJSON(scriptName, _scriptExecutor));
				_broadcaster.sendEvent(eventType, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}




		
	}

}
