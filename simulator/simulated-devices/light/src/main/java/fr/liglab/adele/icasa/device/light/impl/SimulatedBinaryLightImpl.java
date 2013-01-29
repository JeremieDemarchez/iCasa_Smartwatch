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
package fr.liglab.adele.icasa.device.light.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.Zone;

/**
 * Implementation of a simulated binary light device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "iCASA.BinaryLight")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedBinaryLightImpl extends AbstractDevice implements BinaryLight, SimulatedDevice {

	@ServiceProperty(name = BinaryLight.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	private String fault;

	@LogConfig
	private ComponentLogger m_logger;

	/**
	 * Influence zone corresponding to the zone with highest level where the
	 * device is located
	 */
	private Zone m_zone;

	public SimulatedBinaryLightImpl() {
		super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);		
		super.setPropertyValue(BinaryLight.LIGHT_MAX_ILLUMINANCE, 100.0d);
		super.setPropertyValue(BinaryLight.LIGHT_POWER_STATUS, false);
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized boolean getPowerStatus() {
		Boolean powerStatus = (Boolean) getPropertyValue(BinaryLight.LIGHT_POWER_STATUS);
		if (powerStatus == null)
			return false;
		return powerStatus;
	}

	@Override
	public synchronized boolean setPowerStatus(boolean status) {
		setPropertyValue(BinaryLight.LIGHT_POWER_STATUS, (Boolean) status);
		return status;
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) {
		if (propertyName.equals(BinaryLight.LIGHT_POWER_STATUS)) {
			boolean previousStatus = getPowerStatus();

			boolean status = (value instanceof String) ? Boolean.parseBoolean((String) value) : (Boolean) value;

			
			
			if (previousStatus != status) {
				super.setPropertyValue(BinaryLight.LIGHT_POWER_STATUS, status);
				// Trying to modify zone variable
				if (m_zone != null) {
					try {
						m_zone.setVariableValue("Illuminance", computeIlluminance());
					} catch (Exception e) {
						m_logger.error("Variiable Illuminance does not exist in zone " + m_zone.getId());
					}
				}
			}
		} else
			super.setPropertyValue(propertyName, value);
	}

	/**
	 * Return the illuminance currently emitted by this light, according to its
	 * state.
	 * 
	 * @return the illuminance currently emitted by this light
	 */
	private double computeIlluminance() {
		double maxIlluminance = (Double) getPropertyValue(BinaryLight.LIGHT_MAX_ILLUMINANCE);
		return getPowerStatus() ? maxIlluminance : 0.0d;
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
	public void enterInZones(List<Zone> zones) {
		if (!zones.isEmpty()) {
			m_zone = zones.get(0);
			setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, m_zone.getId());
		}
	}

	@Override
	public void leavingZones(List<Zone> zones) {
		m_zone = null;
		setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
	}

}
