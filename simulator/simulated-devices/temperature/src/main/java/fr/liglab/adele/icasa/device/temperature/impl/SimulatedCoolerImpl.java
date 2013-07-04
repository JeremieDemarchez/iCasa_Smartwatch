/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
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
package fr.liglab.adele.icasa.device.temperature.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;

/**
 * Implementation of a simulated cooler device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "iCasa.Cooler")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedCoolerImpl extends AbstractDevice implements Cooler, SimulatedDevice {

	@ServiceProperty(name = Cooler.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	private Thread m_updaterThread;

	private volatile long m_lastUpdateTime;

	private Zone m_zone;

	public SimulatedCoolerImpl() {
        super();
        super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(Cooler.COOLER_POWER_LEVEL, 0.0d);
        super.setPropertyValue(Cooler.COOLER_MAX_POWER_LEVEL, 1000.0d);
        super.setPropertyValue(Cooler.COOLER_UPDATE_PERIOD, 5000);
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Validate
	public synchronized void start() {
		m_updaterThread = new Thread(new UpdaterThread(), "CoolerUpdaterThread-" + m_serialNumber);
		m_updaterThread.start();
		m_lastUpdateTime = System.currentTimeMillis();
	}

	@Invalidate
	public synchronized void stop() throws InterruptedException {
		m_updaterThread.interrupt();
		m_updaterThread.join();
	}

	@Override
	public synchronized double getPowerLevel() {		
		Double powerLevel = (Double) getPropertyValue(Cooler.COOLER_POWER_LEVEL);
		if (powerLevel == null)
			return 0.0d;
		return powerLevel;
	}

	@Override
	public synchronized double setPowerLevel(double level) {
		if (level < 0.0d || level > 1.0d || Double.isNaN(level)) {
			throw new IllegalArgumentException("Invalid power level : " + level);
		}
		setPropertyValue(Cooler.COOLER_POWER_LEVEL, level);
		return level;
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) {
		if (propertyName.equals(Cooler.COOLER_POWER_LEVEL)) {

			double previousLevel = getPowerLevel();		
			double level = (value instanceof String) ? Double.parseDouble((String)value) : (Double) value;

			if (previousLevel!=level) {
				super.setPropertyValue(Cooler.COOLER_POWER_LEVEL, level);
				//m_logger.debug("Power level set to " + level);				
			}			
		} else
			super.setPropertyValue(propertyName, value);
	}


	/**
	 * Return the current temperature produced by the heater, according to its
	 * powerLevel.
	 * The formula used to compute the temperature is :
	 * CurrentTemperature [K]=(P[W]/C[J/K])*t[s]+T0[K]
	 * @return the temperature currently produced by this heater
	 * @author jeremy savonet
	 */
	private double computeTemperature() {
		long time = System.currentTimeMillis();
		double timeDiff = ((double) (time - m_lastUpdateTime)) / 1000.0d;
		m_lastUpdateTime = time;

		//Define constant to compute the value of the thermal capacity
		double airMassCapacity=1000; //mass capacity of the air in J/(Kg.K)
		double airMass = 1.2; //mass of the air in Kg/m^3
		Double roomVolume=0.0; //volume of the room in m^3
		double thermalCapacity = 0.0; //Thermal capacity used to compute the temperature. Expressed in J/K.

		Double currentTemperature = 0.0;
		double returnedTemperature = 0.0;
		double coolerPowerLevel = 0.0; 		

		if (m_zone != null) {

			try {
				currentTemperature = (Double) m_zone.getVariableValue("Temperature");
				roomVolume = (Double) m_zone.getVariableValue("Volume");				
				coolerPowerLevel = getPowerLevel()*getMaxPowerLevel();

				if (roomVolume>0) {
					thermalCapacity = airMass * roomVolume * airMassCapacity;
					returnedTemperature = ((-coolerPowerLevel*timeDiff)/thermalCapacity)+currentTemperature;
					//clippinp function
					if(returnedTemperature < 283.16) returnedTemperature = 283.16;
				}								
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
		return returnedTemperature;
	}

	/**
	 * The updater thread that updates the current temperature and notify
	 * listeners periodically.
	 * 
	 * @author Gabriel Pedraza Ferreira
	 */
	private class UpdaterThread implements Runnable {

		@Override
		public void run() {
			boolean isInterrupted = false;
			while (!isInterrupted) {
				try {
					int sleepTime = (Integer) getPropertyValue(Cooler.COOLER_UPDATE_PERIOD);
					Thread.sleep(sleepTime);
					synchronized (SimulatedCoolerImpl.this) {
						if (m_zone != null) {
							m_zone.setVariableValue("Temperature", computeTemperature());	
						}
					}
				} catch (InterruptedException e) {
					isInterrupted = true;
				}
			}
		}
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
		Double maxLevel = (Double) getPropertyValue(Cooler.COOLER_MAX_POWER_LEVEL);
		if (maxLevel==null)
			return 0;
		return maxLevel;
	}

}
