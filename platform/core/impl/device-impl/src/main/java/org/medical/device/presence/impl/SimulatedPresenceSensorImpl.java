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
package org.medical.device.presence.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.SimulatedEnvironmentListener;

/**
 * Implementation of a simulated presence sensor device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION)})
public class SimulatedPresenceSensorImpl extends AbstractDevice implements
        PresenceSensor, SimulatedDevice, SimulatedEnvironmentListener {

    @ServiceProperty(name = PresenceSensor.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;
    
    @Property(name = "presence.threshold", value = "0.9d")
    private double m_threshold;

    @ServiceProperty(name = PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE, value = "false")
    private boolean m_currentPresence;

    @ServiceProperty(name = "state", value = "deactivated")
    private String state;
    
    @ServiceProperty(name = "fault", value = "no")
    private String fault;
    
    private static final Logger m_logger = LoggerFactory.getLogger(SimulatedPresenceSensorImpl.class);
    
    private volatile SimulatedEnvironment m_env;


    public String getSerialNumber() {
        return m_serialNumber;
    }


    public synchronized boolean getSensedPresence() {
        return m_currentPresence;
    }


    public synchronized void bindSimulatedEnvironment(
            SimulatedEnvironment environment) {
        m_env = environment;
        m_env.addListener(this);
        m_logger.debug("Bound to simulated environment "
                + environment.getEnvironmentId());
    }


    public synchronized String getEnvironmentId() {
        return m_env != null ? m_env.getEnvironmentId() : null;
    }


    public synchronized void unbindSimulatedEnvironment(
            SimulatedEnvironment environment) {
        m_env.removeListener(this);
        m_env = null;
        m_logger.debug("Unbound from simulated environment "
                + environment.getEnvironmentId());
    }
    

    public void environmentPropertyChanged(final String propertyName, final Double oldValue, final Double newValue) {
        if (SimulatedEnvironment.PRESENCE.equals(propertyName)) {
            final boolean oldPresence = m_currentPresence;
            final double presence = newValue.doubleValue();
            if (presence >= m_threshold) {
                m_currentPresence = true;
            } else {
                m_currentPresence = false;
            }
            if (oldPresence != m_currentPresence) {
                m_logger.debug("Sensed presence : " + m_currentPresence);
                notifyListeners();
            }
        }
    }


   public String getLocation() {
      return getEnvironmentId();
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
