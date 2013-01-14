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
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.Zone;
import fr.liglab.adele.icasa.simulator.listener.util.BaseZoneListener;

/**
 * Implementation of a simulated photometer device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "iCASA.Photometer")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedPhotometerImpl extends AbstractDevice implements Photometer, SimulatedDevice {

	@ServiceProperty(name = Photometer.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	private String fault;

	@LogConfig
	private ComponentLogger m_logger;

	private Zone m_zone;

	private PhotometerZoneListener listener = new PhotometerZoneListener();

	public SimulatedPhotometerImpl() {
		setPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE, 0.0d);
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized double getIlluminance() {
		return (Double) getPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE);
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
				if (zone.getVariableValue("Illuminance") != null) {
					m_zone = zone;
					getIlluminanceFromZone();
					m_zone.addListener(listener);
					break;
				}
         }
		}
		if (m_zone==null) 
			setPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE, null);
	}

	@Override
	public void leavingZones(List<Zone> zones) {
		setPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE, null);
		if (!zones.isEmpty()) {
			if (m_zone!=null)
				m_zone.removeListener(listener);
		}
	}

	private void getIlluminanceFromZone() {
		if (m_zone != null) {
			Double illuminanceBefore = (Double) getPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE);
			Double currentIlluminance = ((Double) m_zone.getVariableValue("Illuminance"));
			if (currentIlluminance != null) {
				setPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE, currentIlluminance);
				notifyListeners(new DeviceEvent(SimulatedPhotometerImpl.this, DeviceEventType.PROP_MODIFIED,
				      Photometer.PHOTOMETER_CURRENT_ILLUMINANCE, illuminanceBefore));
			}
		}
	}

	class PhotometerZoneListener extends BaseZoneListener {

		@Override
		public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {
			if (!(fault.equalsIgnoreCase("yes"))) {
				if (variableName.equals("Illuminance")) {
					/*
					 * Object illuminanceBefore = (Double)
					 * getPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE);
					 * double currentIlluminance = ((Double)
					 * zone.getVariableValue(variableName)).doubleValue();
					 * setPropertyValue(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE,
					 * currentIlluminance); notifyListeners(new
					 * DeviceEvent(SimulatedPhotometerImpl.this,
					 * DeviceEventType.PROP_MODIFIED,
					 * Photometer.PHOTOMETER_CURRENT_ILLUMINANCE,
					 * illuminanceBefore));
					 */
					getIlluminanceFromZone();
				}
			}
		}
	}

}
