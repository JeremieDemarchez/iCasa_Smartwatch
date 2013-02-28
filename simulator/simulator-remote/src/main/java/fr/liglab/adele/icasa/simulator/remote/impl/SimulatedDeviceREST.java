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
/**
 *
 */
package fr.liglab.adele.icasa.simulator.remote.impl;

import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.remote.SimulatedDeviceManager;
import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="simulator-remote-rest-device")
@Instantiate(name="simulator-remote-rest-device-0")
@Provides(specifications={SimulatedDeviceManager.class})
public class SimulatedDeviceREST implements SimulatedDeviceManager {

    @Requires
    private SimulationManager _simulationMgr;
   
	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.remote.SimulatedDeviceManager#createDevice(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void createDevice(String deviceType, String deviceId,
			Map<String, Object> properties) {
		_simulationMgr.createDevice(deviceType, deviceId, properties);		
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.remote.SimulatedDeviceManager#removeDevice(java.lang.String)
	 */
	@Override
	public void removeDevice(String deviceId) {
        _simulationMgr.removeDevice(deviceId);
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.remote.SimulatedDeviceManager#getDeviceTypes()
	 */
	@Override
	public Set<String> getDeviceTypes() {
		return _simulationMgr.getSimulatedDeviceTypes();
	}
    
  
}
