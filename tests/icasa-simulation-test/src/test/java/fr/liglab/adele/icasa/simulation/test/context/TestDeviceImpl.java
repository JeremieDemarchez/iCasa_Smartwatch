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
package fr.liglab.adele.icasa.simulation.test.context;

import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;

//@Component(name="TestDeviceComponent")
//@Provides
public class TestDeviceImpl extends AbstractDevice implements SimulatedDevice, TestDevice {

	//@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;
	
	//@ServiceProperty(name = "location", mandatory = true, value="bathroom")
	private String m_location;
	
	//@ServiceProperty(name = "rank", mandatory = true, value="100")
	private int rank = 100;
	
	
	public String getSerialNumber() {
		return m_serialNumber;
	}


	public String getLocation() {
	   return m_location;
   }


}
