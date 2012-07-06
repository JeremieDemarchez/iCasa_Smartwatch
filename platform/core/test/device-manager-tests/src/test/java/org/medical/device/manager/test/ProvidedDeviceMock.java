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
package org.medical.device.manager.test;

import org.medical.common.StateVariable;
import org.medical.device.manager.ProvidedDevice;
import org.medical.device.manager.Service;
import org.medical.device.manager.util.AbstractProvidedDevice;

public class ProvidedDeviceMock extends AbstractProvidedDevice implements
		ProvidedDevice {

	public ProvidedDeviceMock(String id, String name, String vendor) {
		super(id, name, vendor);
	}
	
	public ProvidedDeviceMock(String id, String name, String vendor,
			String typeId) {
		super(id, name, vendor, typeId);
	}

	public void addVariable(StateVariable var) {
		super.addStateVariable(var);
	}
	
	public void addServ(Service service) {
		super.addService(service);
	}
}
