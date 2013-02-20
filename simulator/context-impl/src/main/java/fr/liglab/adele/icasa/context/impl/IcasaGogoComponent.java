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
package fr.liglab.adele.icasa.context.impl;

import java.util.List;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

import fr.liglab.adele.icasa.context.ContextManager;
import fr.liglab.adele.icasa.context.LocatedDevice;
import fr.liglab.adele.icasa.context.Zone;

@Component
@Provides(properties = {
      @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{listZones, listDevices, listZoneVariables, listDeviceProperties}") })
@Instantiate
public class IcasaGogoComponent {

	@Requires
	private ContextManager manager;

	public void listZones() {
		System.out.println("Zones: ");
		List<Zone> zones = manager.getZones();
		for (Zone zone : zones) {
			System.out.println("Zone " + zone.getId() + " des " + zone);
		}
	}

	public void listDevices() {
		System.out.println("Devices: ");
		List<LocatedDevice> devices = manager.getDevices();
		for (LocatedDevice locatedDevice : devices) {
			System.out.println("Device " + locatedDevice);
		}
	}


	public void listZoneVariables(String zoneId) {

		System.out.println("Variables: ");
		Set<String> variables = manager.getZoneVariables(zoneId);
		for (String variable : variables) {
			System.out.println("Variable: " + variable + " - Value: " + manager.getZoneVariableValue(zoneId, variable));
		}
	}

	
	public void listDeviceProperties(String deviceId) {

		System.out.println("Properties: ");
		LocatedDevice device = manager.getDevice(deviceId);
		if (device==null)
			return;
		
		Set<String> properties = device.getProperties();
		for (String property : properties) {
			System.out.println("Property: " + property + " - Value: " +device.getPropertyValue(property));
		}
	}
}
