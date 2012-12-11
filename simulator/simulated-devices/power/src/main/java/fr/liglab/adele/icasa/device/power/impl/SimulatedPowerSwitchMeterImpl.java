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
package fr.liglab.adele.icasa.device.power.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;
import org.ow2.chameleon.handies.ipojo.log.LogConfig;
import org.ow2.chameleon.handies.log.ComponentLogger;

import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.power.PowerSwitchmeter;
import fr.liglab.adele.icasa.device.power.Powermeter;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.Zone;

/**
 * Implementation of a simulated Power Switch + Meter
 * 
 * @author gunalp
 * 
 */
@Component(name="iCASA.PowerSwitchMeter")
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedPowerSwitchMeterImpl extends AbstractDevice implements
		PowerSwitchmeter, SimulatedDevice {

	@ServiceProperty(name = AbstractDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;
	
	@ServiceProperty(name = PowerSwitch.POWERSWITCH_CURRENT_STATUS, value = "false")
	private boolean m_currentStatus;
	
	@ServiceProperty(name = Powermeter.POWERMETER_CURRENT_RATING, value = "NaN")
	private double m_currentRating;
	
	@ServiceProperty(name = "state", value = "deactivated")
    private String state;
    
    @ServiceProperty(name = "fault", value = "no")
    private String fault;
	
	@Property(name = "power.attachedDevice.name", mandatory = true)	
	private String m_attachedDeviceName;
	
	@Property(name = "power.attachedDevice.watt", mandatory = true)	
	private double m_attachedDeviceWatt;
	
	@LogConfig
    private ComponentLogger m_logger;
	
	private volatile SimulatedEnvironment m_env;
	
	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public synchronized boolean getStatus() {
		return m_currentStatus;
	}

	@Override
	public synchronized boolean switchOn() {
		if(m_currentStatus){
			return false;
		}else{
			m_currentRating = m_attachedDeviceWatt;
			m_currentStatus=true;
			return true;
		}
	}

	@Override
	public synchronized boolean switchOff() {
		if(!m_currentStatus){
			return false;
		}else{
			m_currentRating = 0.0d;
			m_currentStatus=false;
			return true;
		}
	}

	@Override
	public double getCurrentPowerRating() {
		return m_currentRating;
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
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void leavingZones(List<Zone> zones) {
	   // TODO Auto-generated method stub
	   
   } 
}
