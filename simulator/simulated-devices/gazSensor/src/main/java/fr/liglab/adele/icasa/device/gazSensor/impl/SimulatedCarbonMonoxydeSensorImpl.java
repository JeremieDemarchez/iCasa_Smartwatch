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
package fr.liglab.adele.icasa.device.gazSensor.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.gazSensor.CarbonMonoxydeSensor;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.listener.util.BaseZoneListener;

/**
 * Implementation of a CO sensor device.
 * 
 * @author jeremy
 */
@Component(name = "iCASA.GazSensor")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedCarbonMonoxydeSensorImpl extends AbstractDevice implements CarbonMonoxydeSensor, SimulatedDevice {

	@ServiceProperty(name = CarbonMonoxydeSensor.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	private volatile Zone m_zone;

	private ZoneListener listener = new GazSensorZoneListener();

	public SimulatedCarbonMonoxydeSensorImpl() {
		super();
		setPropertyValue(CarbonMonoxydeSensor.CO_CURRENT_CONCENTRATION, 0.0);
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public double getCOConcentration() {
		return (Double) getPropertyValue(CarbonMonoxydeSensor.CO_CURRENT_CONCENTRATION);
	}
	
	@Validate
	public synchronized void start() {

	}

	@Invalidate
	public synchronized void stop() throws InterruptedException {

	}


	@Override
	public void enterInZones(List<Zone> zones) {
		if (!zones.isEmpty()) {
			for (Zone zone : zones) {
				if (zone.getVariableValue("COConcentration") != null) {
					m_zone = zone;
					getCOConcentrationFromZone();
					m_zone.addListener(listener);
					break;
				}
			}
		}
	}

	@Override
	public void leavingZones(List<Zone> zones) {
		setPropertyValue(CarbonMonoxydeSensor.CO_CURRENT_CONCENTRATION, null);
		if (m_zone != null)
			m_zone.removeListener(listener);
	}

	private void getCOConcentrationFromZone() {
		if (m_zone != null) {
			Object currentCOConcentration = m_zone.getVariableValue("COConcentration");
			if (currentCOConcentration != null)
				setPropertyValue(CarbonMonoxydeSensor.CO_CURRENT_CONCENTRATION, currentCOConcentration);
		}
	}

	class GazSensorZoneListener extends BaseZoneListener {

		@Override
		public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {

			if (m_zone == zone) {
				if (!(getFault().equalsIgnoreCase("yes")))
					if (variableName.equals("COConcentration"))
						getCOConcentrationFromZone();
			}
		}
	}
}
