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
package fr.liglab.adele.icasa.device.impl;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;

/**
 * Implementation of a simulated cooler device.
 * 
 * @author bourretp
 */
@Component(name="iCASA.Cooler")
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedCoolerImpl extends AbstractDevice implements Cooler,
        SimulatedDevice {

    @ServiceProperty(name = Cooler.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    @ServiceProperty(name = Cooler.COOLER_POWER_LEVEL, value = "0.0d")
    private double m_powerLevel;
    
    @ServiceProperty(name = "state", value = "deactivated")
    private String state;
    
    @ServiceProperty(name = "fault", value = "no")
    private String fault;

    // Unit = K.s^-1.m^-3
    @Property(name = "cooler.maxCapacity", value = "1.0d")
    private double m_maxCapacity;

    @Property(name = "updaterThread.period", value = "5000")
    private long m_period;

    @LogConfig
    private ComponentLogger m_logger;

    private volatile SimulatedEnvironment m_env;

    private Thread m_updaterThread;

    private volatile long m_lastUpdateTime;

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Validate
    public synchronized void start() {
        m_updaterThread = new Thread(new UpdaterThread(),
                "CoolerUpdaterThread-" + m_serialNumber);
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
        return m_powerLevel;
    }

    @Override
    public synchronized double setPowerLevel(double level) {
        if (level < 0.0d || level > 1.0d || Double.isNaN(level)) {
            throw new IllegalArgumentException("Invalid power level : " + level);
        }
        if (m_env != null) {
            notifyEnvironment();
        }
        double save = m_powerLevel;
        m_powerLevel = level;
        m_logger.debug("Power level set to " + level);
        notifyListeners(new DeviceEvent(this, DeviceEventType.PROP_MODIFIED, Cooler.COOLER_POWER_LEVEL, save));
        return save;
    }

    /**
     * Notify the bound simulated environment that the temperature has changed.
     *
     *            the temperature difference
     */
    private void notifyEnvironment() {
        m_env.lock();
        try {
            long time = System.currentTimeMillis();
            double timeDiff = ((double) (time - m_lastUpdateTime)) / 1000.0d;
            m_lastUpdateTime = time;
            double current = m_env
                    .getProperty(SimulatedEnvironment.TEMPERATURE);
            double volume = m_env.getProperty(SimulatedEnvironment.VOLUME);
            double decrease = m_maxCapacity * m_powerLevel * timeDiff / volume;
            if (current > decrease) {
                m_env.setProperty(SimulatedEnvironment.TEMPERATURE, current
                        - decrease);                
            } else {
                m_env.setProperty(SimulatedEnvironment.TEMPERATURE, 0.0d);           
            }
            
        } finally {
            m_env.unlock();
        }
    }

    /**
     * The updater thread that updates the current temperature and notify
     * listeners periodically.
     * 
     * @author bourretp
     */
    private class UpdaterThread implements Runnable {

        @Override
        public void run() {
            boolean isInterrupted = false;
            while (!isInterrupted) {
                try {
                    Thread.sleep(m_period);
                    synchronized (SimulatedCoolerImpl.this) {
                        if (m_env != null) {
                            notifyEnvironment();
                        }
                    }
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
        }
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

}
