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

	/**
	 * Influence zone corresponding to the zone with highest level where the
	 * device is located
	 */
	private Zone m_zone;

	public SimulatedBinaryLightImpl() {
		super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);		
		super.setPropertyValue(BinaryLight.LIGHT_MAX_ILLUMINANCE, 0.0d);
		super.setPropertyValue(BinaryLight.LIGHT_POWER_STATUS, false);
		super.setPropertyValue(BinaryLight.LIGHT_MAX_POWER_LEVEL, 100.0d);
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
					}
				}
			}
		} else
			super.setPropertyValue(propertyName, value);
	}

	/**
	 * Return the illuminance currently emitted by this light, according to its
	 * state.
	 * The formula used to compute the illuminance is :
	 * Illuminance [cd/m² or lux]=(power[W]*680.0[lumens])/surface[m²] 
	 * @return the illuminance currently emitted by this light
	 */
	private double computeIlluminance() {
	
		double returnedIlluminance=0.0;
		int height = m_zone.getHeight();
		int width = m_zone.getWidth();
		double surface = 0;				
		double powerLevel = getPowerStatus() ? getMaxPowerLevel() : 0.0d;
		double scaleFactor = 0.014d; //1px -> 0.014m
		double lumens = 680.0d; //Rought Constant to establish the correspondance between power & illuminance
		
		surface = scaleFactor*scaleFactor*height*width;
		returnedIlluminance = (powerLevel*lumens)/surface;

		return getPowerStatus() ? returnedIlluminance : 0.0d;			
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
	
	@Override
	public double getMaxPowerLevel() {
		Double maxLevel = (Double) getPropertyValue(BinaryLight.LIGHT_MAX_POWER_LEVEL);
		if (maxLevel==null)
			return 0;
		return maxLevel;
	}

}
