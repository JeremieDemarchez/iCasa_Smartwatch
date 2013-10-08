/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.liglab.adele.icasa.device.light.BinaryLight;

@Component(name = "ConsumerFakeDevice")
public class ConsumerFakeDevice {

	//policy="dynamic-priority"
	@Bind(id = "dev", aggregate=true)
	public void bindFakeDevice(BinaryLight device) {
		System.out.println("--------------> Device Binded ----- " + device.getSerialNumber());
	}

	@Unbind(id = "dev")
	public void unbindFakeDevice(BinaryLight device) {
		System.out.println("--------------> Device Unbinded ----- " + device.getSerialNumber());
	}

}
