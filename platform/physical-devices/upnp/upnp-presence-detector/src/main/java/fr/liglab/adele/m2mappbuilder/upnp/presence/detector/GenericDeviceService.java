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
package fr.liglab.adele.m2mappbuilder.upnp.presence.detector;

import java.util.HashMap;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

public class GenericDeviceService implements UPnPService {

	final private String SERVICE_ID = "urn:schemas-upnp-org:serviceId:medical-device:1";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:medical-device:1";
	final private String VERSION ="1";
	
	private UPnPStateVariable[] stateVariables;
	private HashMap actions = new HashMap();
	
	private DeviceStateStateVariable state;
	
	private DeviceFaultStateVariable fault;
	
	public GenericDeviceService() {
		state = new DeviceStateStateVariable();
		fault = new DeviceFaultStateVariable();
		
		this.stateVariables = new UPnPStateVariable[]{state, fault};
		
		//UPnPAction presenceAction = new DetectPresenceAction(presence);
		//actions.put(presenceAction.getName(),presenceAction);

   }

	@Override
	public String getId() {
		return SERVICE_ID;
	}

	@Override
	public String getType() {
		return SERVICE_TYPE;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public UPnPAction getAction(String name) {
		return (UPnPAction)actions.get(name);
	}

	@Override
	public UPnPAction[] getActions() {
		return (UPnPAction[])(actions.values()).toArray(new UPnPAction[]{});
	}

	@Override
	public UPnPStateVariable[] getStateVariables() {
		return stateVariables;
	}
	@Override
	public UPnPStateVariable getStateVariable(String name) {
		if (name.equals("state"))
			return state;
		if (name.equals("fault"))
			return fault;
		return null;
	}

}
