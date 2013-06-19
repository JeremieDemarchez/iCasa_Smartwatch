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
package test.context.source;

import java.util.Random;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;

@Component(name="FakeDeviceComponent")
@Provides
public class FakeDeviceLocation extends AbstractDevice implements SimulatedDevice, FakeDevice {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;
	
	@ServiceProperty(name = "location", mandatory = true, value="bathroom")
	private String m_location;
	
	@ServiceProperty(name = "rank", mandatory = true, value="100")
	private int rank = 100;
	
	
	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Validate
	private void star() {
		rank = (new Random()).nextInt(100);
   }


}
