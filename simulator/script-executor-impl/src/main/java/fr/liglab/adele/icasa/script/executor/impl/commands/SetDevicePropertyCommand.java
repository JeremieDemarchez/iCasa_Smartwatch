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
package fr.liglab.adele.icasa.script.executor.impl.commands;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.LocatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;

@Component(name = "SetDevicePropertyCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{setDeviceProperty}"),
      @StaticServiceProperty(name = "name", value = "set-property", type = "String") })
@Instantiate(name="property-device-command")
public class SetDevicePropertyCommand extends DeviceCommand {

	
	@Requires
	private SimulationManager simulationManager;
	
	private String propertyId;
	
	private Object value;

	@Override
   public Object execute() throws Exception {
		LocatedDevice device = simulationManager.getDevice(deviceId);
		System.out.println("Trying to modifiy " + propertyId + " property " );
		if (device!=null)
			device.setPropertyValue(propertyId, value);		
		return null;
   }
	
	@Override
	public void configure(JSONObject param) throws Exception {
		super.configure(param);
		propertyId = param.getString("propertyId");
		value = param.get("value");
	}
	
	public void setDeviceProperty(String deviceId, String propertyId, Object value) throws Exception {
		this.deviceId = deviceId;
		this.propertyId = propertyId;
		this.value = value;		
		execute();
	}

}
