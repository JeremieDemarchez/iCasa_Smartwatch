/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
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
		super();
        super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
		super.setPropertyValue(DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL, 100.0d);
		super.setPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL, 0.0d);
		
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized double getPowerLevel() {
		Double powerLevel = (Double) getPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL);
		if (powerLevel == null)
			return 0.0d;
		return powerLevel;
	}

	@Override
	public synchronized double setPowerLevel(double level) {
		if (level < 0.0d || level > 1.0d || Double.isNaN(level)) 
			throw new IllegalArgumentException("Invalid power level : " + level);
		//Add by jeremy
		setPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL, level);

		return level;
	}
	
	@Override
	public double getMaxPowerLevel() {
		Double maxLevel = (Double) getPropertyValue(DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL);
		if (maxLevel==null)
			return 0;
		return maxLevel;
	}
}
