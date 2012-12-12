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
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Updated;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.Zone;
import fr.liglab.adele.icasa.environment.listener.ZoneListener;
import fr.liglab.adele.icasa.environment.listener.ZonePropListener;
import fr.liglab.adele.icasa.environment.listener.impl.BaseZoneListener;

/**
 * Implementation of a simulated thermometer device.
 * 
 * @author bourretp
 */
@Component(name="iCASA.Thermometer")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedThermometerImpl extends AbstractDevice implements Thermometer, SimulatedDevice {

	@ServiceProperty(name = Thermometer.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@Property(name = Thermometer.THERMOMETER_CURRENT_TEMPERATURE, value = "0.0")
	@ServiceProperty(name = Thermometer.THERMOMETER_CURRENT_TEMPERATURE, value = "0.0")
	private double m_currentTemperature;

	@Property(name = "updaterThread.period", value = "5000")
	private long m_period;

	@LogConfig
	private ComponentLogger m_logger;

   @ServiceProperty(name = "state", value = "activated")
   private volatile String state;
   
   @Property(name = "fault", value = "no")
   @ServiceProperty(name = "fault", value = "no")
	private volatile String fault;
   
   
   private volatile Zone m_zone;

	private ZoneListener listener = new MyZoneListener();

   //private volatile SimulatedEnvironment m_env;

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized double getTemperature() {
		return m_currentTemperature;
	}

	@Validate
	public synchronized void start() {
		/*
		 * m_updaterThread = new Thread(new UpdaterThread(),
		 * "ThermometerUpdaterThread-" + m_serialNumber); m_updaterThread.start();
		 */
	}

	@Invalidate
	public synchronized void stop() throws InterruptedException {
		/*
		 * m_updaterThread.interrupt(); m_updaterThread.join();
		 */
	}

	/*	
	public void setFault(String fault) {
		this.fault = fault;

		if (fault) {
			if (m_updaterThread != null) {
				try {
					m_updaterThread.interrupt();
					m_updaterThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (m_updaterThread != null) {
				if (m_updaterThread.isAlive()) {
					try {
						m_updaterThread.interrupt();
						m_updaterThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				m_updaterThread = new Thread(new UpdaterThread(), "ThermometerUpdaterThread-" + m_serialNumber);
				m_updaterThread.start();
			} else {
				m_updaterThread = new Thread(new UpdaterThread(), "ThermometerUpdaterThread-" + m_serialNumber);
				m_updaterThread.start();
			}			
		}
		
	}
  */

	/*
    @Override
    public void zoneVariableAdded(Zone zone, String variableName) {
        // do nothing
    }

    @Override
    public void zoneVariableRemoved(Zone zone, String variableName) {
        // do nothing
    }
		*/
	
	/*
    @Override
    public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {

		if (!(fault.equalsIgnoreCase("yes"))) {
			if (SimulatedEnvironment.TEMPERATURE.equals(variableName)) {
                Object tempOldValue = null;
				synchronized (this) {
                    tempOldValue = m_currentTemperature;
		            m_currentTemperature = (Double) zone.getVariableValue(variableName);
	            }
                notifyListeners(new DeviceEvent(this, DeviceEventType.PROP_MODIFIED, Thermometer.THERMOMETER_CURRENT_TEMPERATURE, tempOldValue));
			}			
		}

   }
	*/
	
	@Updated
	public void updated() {
		if (!(fault.equalsIgnoreCase("yes"))) {
			getTemperatureFromEnvironment();
		}
	}
	
	private void getTemperatureFromEnvironment() {
		/*
		synchronized (this) {
			if (m_env != null) {
				m_currentTemperature = m_env.getProperty(SimulatedEnvironment.TEMPERATURE);
			}
		}
		*/
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
	    * @param fault the fault to set
	    */
	   public void setFault(String fault) {
	   	this.fault = fault;
	   }

		@Override
      public void enterInZones(List<Zone> zones) {	      
	      if (!zones.isEmpty()) {
	      	m_zone = zones.get(0);
	      	m_currentTemperature = (Double) m_zone.getVariableValue("Temperature");
	      	m_zone.addListener(listener);
	      	System.out.println("Temperature: " + m_serialNumber + " - " + m_currentTemperature);	      		      	
	      }
      }

		@Override
      public void leavingZones(List<Zone> zones) {
	      if (!zones.isEmpty()) {
	      	m_zone.removeListener(listener);
	      	// System.out.println("Thermometer" +  m_serialNumber + " LEAVING zone " + zones.get(0).getId());	      	
	      }	      
      } 

		
		class MyZoneListener extends BaseZoneListener {

			@Override
			public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {
			   if (m_zone==zone) {
			   	if (variableName.equals("Temperature")) {
			   		m_currentTemperature = (Double) zone.getVariableValue("Temperature");
			   		System.out.println("Temperature: " + m_serialNumber + " - " + m_currentTemperature);
			   	}			   	
			   }
			}			
		}
		
}
