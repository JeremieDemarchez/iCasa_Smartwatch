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
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.bathroomscale.Sphygmometer;
import fr.liglab.adele.icasa.simulator.SimulationManager;

@Component(name = "iCASA.Sphygmometer")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedSphygmometerImpl extends MedicalDeviceImpl implements Sphygmometer {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	@Property(name = "fault", value = "no")
	private String fault;

	@Requires
	private SimulationManager manager;


	public SimulatedSphygmometerImpl() {
		super();
		setPropertyValue(SYSTOLIC_PROPERTY, 0);
		setPropertyValue(DIASTOLIC_PROPERTY, 0);
		setPropertyValue(PULSATIONS_PROPERTY, 0);
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

	/**
	 * sets the state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the fault
	 */
	public String getFault() {
		return fault;
	}

	/**
	 * @param fault
	 *           the fault to set
	 */
	public void setFault(String fault) {
		this.fault = fault;
	}




	@Override
   protected SimulationManager getManager() {
	   return manager;
   }

	@Override
   protected void updateSpecificState() {
		setPropertyValue(SYSTOLIC_PROPERTY, getRandomIntValue(110, 150));
		setPropertyValue(DIASTOLIC_PROPERTY, getRandomIntValue(60, 90));
		setPropertyValue(PULSATIONS_PROPERTY, getRandomIntValue(60, 100));   
   }

	@Override
   protected void resetSpecificState() {
		setPropertyValue(SYSTOLIC_PROPERTY, 0);
		setPropertyValue(DIASTOLIC_PROPERTY, 0);
		setPropertyValue(PULSATIONS_PROPERTY, 0);  
   }

	@Override
   public int getSystolic() {
		Integer value = (Integer) getPropertyValue(SYSTOLIC_PROPERTY);
		if (value != null)
			return value;
	   return 0;
   }

	@Override
   public int getDiastolic() {
		Integer value = (Integer) getPropertyValue(DIASTOLIC_PROPERTY);
		if (value != null)
			return value;
	   return 0;
   }

	@Override
   public int getPulsations() {
		Integer value = (Integer) getPropertyValue(PULSATIONS_PROPERTY);
		if (value != null)
			return value;
	   return 0;
   }
}
