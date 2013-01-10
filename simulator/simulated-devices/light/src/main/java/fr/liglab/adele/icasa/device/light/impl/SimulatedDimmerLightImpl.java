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
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.Zone;

/**
 * Implementation of a simulated dimmer light device.
 * 
 * @author bourretp
 */
@Component(name = "iCASA.DimmerLight")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedDimmerLightImpl extends AbstractDevice implements DimmerLight, SimulatedDevice {

	@ServiceProperty(name = DimmerLight.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = DimmerLight.LIGHT_POWER_LEVEL, value = "0.0d")
	private double m_powerLevel;

	// The maximum illuminance emitted by this light
	@ServiceProperty(name = "light.maxIlluminance", value = "100.0d")
	private double m_maxIlluminance;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	private String fault;

	@LogConfig
	private ComponentLogger m_logger;

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized double getPowerLevel() {
		return m_powerLevel;
	}

	@Override
	public synchronized double setPowerLevel(double level) {

		if (level < 0.0d || level > 1.0d || Double.isNaN(level)) {
			throw new IllegalArgumentException("Invalid power level : " + level);
		}
		double save = m_powerLevel;
		double illuminanceBefore = illuminance();
		m_powerLevel = level;
		double illuminanceAfter = illuminance();
		m_logger.debug("Power level set to " + level);
		notifyListeners(new DeviceEvent(this, DeviceEventType.PROP_MODIFIED, DimmerLight.LIGHT_POWER_LEVEL,
		      illuminanceBefore));
		return save;
	}


	/**
	 * Return the illuminance currently emitted by this light, according to its
	 * state.
	 * 
	 * @return the illuminance currently emitted by this light
	 */
	private double illuminance() {
		return m_powerLevel * m_maxIlluminance;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void leavingZones(List<Zone> zones) {
		// TODO Auto-generated method stub

	}
}
