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
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.Zone;

/**
 * Implementation of a simulated Power Switch + Meter
 * 
 * @author Gabriel Pedraza Ferreira
 * 
 */


@Component(name="iCASA.PowerSwitch")
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedPowerSwitchImpl extends AbstractDevice implements
		PowerSwitch, SimulatedDevice {

	@ServiceProperty(name = AbstractDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	/*
	@LogConfig
    private ComponentLogger m_logger;
	*/
	
	public SimulatedPowerSwitchImpl() {
		setPropertyValue(POWERSWITCH_CURRENT_STATUS, false);
	}
	
	
	//private volatile SimulatedEnvironment m_env;
	
	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}


	@Override
	public synchronized boolean switchOn() {
		setPropertyValue(POWERSWITCH_CURRENT_STATUS, true);
		return getStatus();
	}

	@Override
	public synchronized boolean switchOff() {
		setPropertyValue(POWERSWITCH_CURRENT_STATUS, false);
		return getStatus();
	}

	@Override
   public boolean getStatus() {
	   Boolean status = (Boolean) getPropertyValue(POWERSWITCH_CURRENT_STATUS);
	   if (status==null)
	   	return false;
	   return status;
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
