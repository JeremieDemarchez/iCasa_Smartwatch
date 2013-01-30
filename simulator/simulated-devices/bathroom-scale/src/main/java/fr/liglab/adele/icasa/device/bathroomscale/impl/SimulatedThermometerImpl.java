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
package fr.liglab.adele.icasa.device.bathroomscale.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.bathroomscale.MedicalThermometer;
import fr.liglab.adele.icasa.device.bathroomscale.rest.api.BathroomScaleRestAPI;
import fr.liglab.adele.icasa.device.bathroomscale.rest.api.MedicalThermometerRestAPI;
import fr.liglab.adele.icasa.simulator.SimulationManager;

@Component(name = "iCASA.MedicalThermometer")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedThermometerImpl extends MedicalDeviceImpl implements MedicalThermometer {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@Requires
	private SimulationManager manager;
	
	@Requires(optional = true)
	private MedicalThermometerRestAPI restAPI;

	public SimulatedThermometerImpl() {
		super();
		setPropertyValue(TEMPERATURE_PROPERTY, 0.0f);
	}

	@Validate
	protected void start() {
		manager.addListener(this);
	}

	@Invalidate
	protected void stop() {
		manager.removeListener(this);
	}

	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
   protected SimulationManager getManager() {
	   return manager;
   }

	@Override
   protected void updateSpecificState() {
		float temperature = getRandomFloatValue(30, 40);
	   setPropertyValue(TEMPERATURE_PROPERTY, temperature);	   
		if (restAPI != null) {
			try {
				restAPI.sendMeasure(temperature);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
   }

	@Override
   protected void resetSpecificState() {
		setPropertyValue(TEMPERATURE_PROPERTY, 0.0f);	   
   }

	@Override
   public float getCurrentTemperature() {
		Float value = (Float) getPropertyValue(TEMPERATURE_PROPERTY);
		if (value != null)
			return value;
		return 0.0f;
   }
}
