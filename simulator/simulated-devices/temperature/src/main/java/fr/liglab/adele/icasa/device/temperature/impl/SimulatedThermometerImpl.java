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
package fr.liglab.adele.icasa.device.temperature.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.Zone;
import fr.liglab.adele.icasa.simulator.listener.ZoneListener;
import fr.liglab.adele.icasa.simulator.listener.util.BaseZoneListener;

/**
 * Implementation of a simulated thermometer device.
 * 
 * @author bourretp
 */
@Component(name = "iCASA.Thermometer")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedThermometerImpl extends AbstractDevice implements Thermometer, SimulatedDevice {

	@ServiceProperty(name = Thermometer.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "activated")
	private volatile String state;

	@ServiceProperty(name = "fault", value = "no")
	private volatile String fault;

	private volatile Zone m_zone;

	private ZoneListener listener = new ThermometerZoneListener();

	public SimulatedThermometerImpl() {
		setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, 0.0);
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized double getTemperature() {
		return (Double) getPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE);
	}

	@Validate
	public synchronized void start() {

	}

	@Invalidate
	public synchronized void stop() throws InterruptedException {

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
			for (Zone zone : zones) {
				if (zone.getVariableValue("Temperature") != null) {
					m_zone = zone;
					getTemperatureFromZone();
					m_zone.addListener(listener);
					break;
				}
			}
		}
	}

	@Override
	public void leavingZones(List<Zone> zones) {
		setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, null);
		if (m_zone != null)
			m_zone.removeListener(listener);
	}

	private void getTemperatureFromZone() {
		if (m_zone != null) {
			Object currentTemperature = m_zone.getVariableValue("Temperature");
			if (currentTemperature != null)
				setPropertyValue(Thermometer.THERMOMETER_CURRENT_TEMPERATURE, currentTemperature);
		}
	}

	class ThermometerZoneListener extends BaseZoneListener {

		@Override
		public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {

			if (m_zone == zone) {
				if (!(fault.equalsIgnoreCase("yes")))
					if (variableName.equals("Temperature"))
						getTemperatureFromZone();
			}
		}
	}

}
