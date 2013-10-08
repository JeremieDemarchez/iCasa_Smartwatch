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
package test.component.handler;


import org.apache.felix.ipojo.annotations.Component;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.light.BinaryLight;

@Component(name="ComponentUsingArray")
public class ComponentUsingArray {
	
	@RequiresDevice(id="thermometers", type="field")
	private BinaryLight[] lights;

	protected void start() {
	    System.out.println("===> Array Size : " + lights.length);
	    System.out.println("===> Array Size : " + DeviceListener.class.getName());
    }
	
	public BinaryLight[] getLights() {
        return lights;
    }

	
	
}