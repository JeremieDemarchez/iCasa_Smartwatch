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

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
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

	@ServiceProperty(name = BinaryLight.LIGHT_POWER_STATUS, value = "false")
	private volatile boolean m_powerStatus;

	// The maximum illuminance emitted by this light
	@ServiceProperty(name = "light.maxIlluminance", value = "100.0d")
	private double m_maxIlluminance;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	private String fault;

	@LogConfig
	private ComponentLogger m_logger;

	/**
	 * Influence zone corresponding to the zone with highest level where the device is located
	 */
	private Zone m_zone;

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized boolean getPowerStatus() {
		return m_powerStatus;
	}

	@Override
	public synchronized boolean setPowerStatus(boolean status) {

		boolean save = m_powerStatus;
		double illuminanceBefore = computeIlluminance();
		m_powerStatus = status;
		double illuminanceAfter = computeIlluminance();
		m_logger.debug("Power status set to " + status);

		notifyListeners(new DeviceEvent(this, DeviceEventType.PROP_MODIFIED, BinaryLight.LIGHT_POWER_STATUS,
		      illuminanceBefore));

		// Trying to modify zone variable
		if (m_zone != null) {
			try {
				m_zone.setVariableValue("Illuminance", illuminanceAfter);
			} catch (Exception e) {
				m_logger.error("Variiable Illuminance does not exist in zone " + m_zone.getId());
			}
		}

		return save;
	}

	/**
	 * Return the illuminance currently emitted by this light, according to its
	 * state.
	 * 
	 * @return the illuminance currently emitted by this light
	 */
	private double computeIlluminance() {
		return m_powerStatus ? m_maxIlluminance : 0.0d;
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
		}
	}

	@Override
	public void leavingZones(List<Zone> zones) {
		m_zone = null;
	}

}
