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

import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;

/**
 * Implementation of a simulated dimmer light device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "iCASA.DimmerLight")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedDimmerLightImpl extends AbstractDevice implements DimmerLight, SimulatedDevice {

	@ServiceProperty(name = DimmerLight.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	/**
	 * Influence zone corresponding to the zone with highest level where the device is located
	 */
	private Zone m_zone;

	public SimulatedDimmerLightImpl() {
		super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
		super.setPropertyValue(DimmerLight.LIGHT_MAX_ILLUMINANCE, 0.0d);
		super.setPropertyValue(DimmerLight.LIGHT_MAX_POWER_LEVEL, 100.0d);
		super.setPropertyValue(DimmerLight.LIGHT_POWER_LEVEL, 0.0d);
		
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized double getPowerLevel() {
		Double powerLevel = (Double) getPropertyValue(DimmerLight.LIGHT_POWER_LEVEL);
		if (powerLevel == null)
			return 0.0d;
		return powerLevel;
	}

	@Override
	public synchronized double setPowerLevel(double level) {
		if (level < 0.0d || level > 1.0d || Double.isNaN(level)) 
			throw new IllegalArgumentException("Invalid power level : " + level);
		//Add by jeremy
		setPropertyValue(DimmerLight.LIGHT_POWER_LEVEL, level);

		return level;
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) {
		if (propertyName.equals(DimmerLight.LIGHT_POWER_LEVEL)) {
			double previousLevel = getPowerLevel();

			double level = (value instanceof String) ? Double.parseDouble((String)value) : (Double) value;

			if (previousLevel!=level) {
				super.setPropertyValue(DimmerLight.LIGHT_POWER_LEVEL, level);
				// Trying to modify zone variable
				if (m_zone!=null) {
					try {
						m_zone.setVariableValue("Illuminance", computeIlluminance());
					} catch (Exception e) {
						e.printStackTrace();
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
		double powerLevel = getPowerLevel();
		double scaleFactor = 0.014; //1px -> 0.014m
		double lumens = 680.0d; //Rought Constant to establish the correspondance between power & illuminance
		
		surface = scaleFactor*scaleFactor*height*width;
		returnedIlluminance = (powerLevel*getMaxPowerLevel()*lumens)/surface;
		return returnedIlluminance;			
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
		Double maxLevel = (Double) getPropertyValue(DimmerLight.LIGHT_MAX_POWER_LEVEL);
		if (maxLevel==null)
			return 0;
		return maxLevel;
	}
}
