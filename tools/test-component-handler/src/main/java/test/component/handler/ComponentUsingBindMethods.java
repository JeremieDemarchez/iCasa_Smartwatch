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

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.liglab.adele.icasa.dependency.handler.annotations.BindDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;


@Component
@Instantiate
public class ComponentUsingBindMethods {

	
	@BindDevice
	public void bindBinaryLight(BinaryLight light) {
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getSerialNumber());
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getPowerStatus());
	}
	
	@Bind
	public void bindBinaryLight2(BinaryLight light) {
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getSerialNumber());
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getPowerStatus());
	}
	
	@Unbind
	public void unbindBinaryLight2(BinaryLight light) {
		
	}
	
	@Unbind
	public void unbindBinaryLight3(BinaryLight light) {
		
	}
	
	

	
	
}
