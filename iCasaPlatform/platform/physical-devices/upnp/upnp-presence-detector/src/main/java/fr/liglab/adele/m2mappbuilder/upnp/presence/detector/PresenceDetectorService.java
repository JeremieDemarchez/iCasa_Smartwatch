/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.m2mappbuilder.upnp.presence.detector;

import java.util.HashMap;

import org.apache.felix.upnp.extra.util.UPnPEventNotifier;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

public class PresenceDetectorService implements UPnPService {

	final private String SERVICE_ID = "urn:schemas-upnp-org:serviceId:presence:1";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:presence:1";
	final private String VERSION ="1";
	
	private UPnPStateVariable[] states;
	private HashMap actions = new HashMap();
	
	private DetectedPresenceStateVariable presence;
	
	
	public PresenceDetectorService(PresenceSensor presenceSensor, boolean isSimulated) {
		presence = new DetectedPresenceStateVariable(presenceSensor, isSimulated);

		this.states = new UPnPStateVariable[]{presence};
		
		//UPnPAction presenceAction = new DetectPresenceAction(presence);
		//actions.put(presenceAction.getName(),presenceAction);

   }

	public String getId() {
		return SERVICE_ID;
	}

	public String getType() {
		return SERVICE_TYPE;
	}

	public String getVersion() {
		return VERSION;
	}

	public UPnPAction getAction(String name) {
		return (UPnPAction)actions.get(name);
	}

	public UPnPAction[] getActions() {
		return (UPnPAction[])(actions.values()).toArray(new UPnPAction[]{});
	}

	public UPnPStateVariable[] getStateVariables() {
		return states;
	}

	public UPnPStateVariable getStateVariable(String name) {
		if (name.equals("Presence"))
			return presence;
		return null;
	}
	
	public void setNotifier(UPnPEventNotifier notifier) {
		presence.setNotifier(notifier);
	}
	
	public PresenceSensor getPresenceSensor() {
		return presence.getPresenceSensor();
	}

	public void setPresenceSensor(PresenceSensor presenceSensor) {
		presence.setPresenceSensor(presenceSensor);
	}

}
