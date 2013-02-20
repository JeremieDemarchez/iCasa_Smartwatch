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

import fr.liglab.adele.icasa.context.Zone;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;

/**
 * Implementation of a simulated cooler device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "iCASA.Cooler")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedCoolerImpl extends AbstractDevice implements Cooler, SimulatedDevice {

	@ServiceProperty(name = Cooler.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;


	private Thread m_updaterThread;

	private volatile long m_lastUpdateTime;

	private Zone m_zone;

	public SimulatedCoolerImpl() {
		setPropertyValue(Cooler.COOLER_POWER_LEVEL, 0.2d);
		setPropertyValue(Cooler.COOLER_UPDATE_PERIOD, 5000);
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
	

	private void calculateTemperature() {
		long time = System.currentTimeMillis();
		double timeDiff = ((double) (time - m_lastUpdateTime)) / 1000.0d;
		m_lastUpdateTime = time;
		if (m_zone != null) {
			
			try {
				Double current = (Double) m_zone.getVariableValue("Temperature");
				Double volume = (Double) m_zone.getVariableValue("Volume");				
				double powerLevel = getPowerLevel();
				
				if (volume>0) {
					double decrease = powerLevel * timeDiff / volume;
					if (decrease>0)
						m_zone.setVariableValue("Temperature", current - decrease);				
				}								
         } catch (Exception e) {
	         e.printStackTrace();
         }				
		}
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
							calculateTemperature();
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

}
