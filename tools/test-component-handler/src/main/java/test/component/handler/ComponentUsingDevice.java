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
package test.component.handler;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

@Component(name="ComponentUsingDevice")
@Instantiate
public class ComponentUsingDevice {
	
	@RequiresDevice(mandatoryProps={"thermometer.currentTemperature", "surface"})
	private Thermometer thermometer;
	
	
	@RequiresDevice(mandatoryProps={"heater.powerLevel"})
	private Heater heater;

	
	@Requires
	private Heater otherHeater;
	
	@Validate
	public void start() {
		System.out.println("Heater SN" + heater.getSerialNumber());
		System.out.println("Thermometer SN" + thermometer.getSerialNumber());
	}
	
	

}
