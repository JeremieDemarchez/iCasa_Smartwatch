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
package fr.liglab.adele.icasa.distribution.test.util;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import fr.liglab.adele.icasa.device.GenericDevice;

public class DeviceTestUtil {

	
	public static ServiceRegistration registerDevice(BundleContext context, GenericDevice device, Class... servicesToRegister) {
		Dictionary serviceProperties = new Hashtable();
		serviceProperties.put(GenericDevice.DEVICE_SERIAL_NUMBER, device.getSerialNumber());// add serial number service
																														// prop
		String[] serviceIntfs = new String[servicesToRegister.length];
		for (int i = 0; i < servicesToRegister.length; i++) {
			serviceIntfs[i] = servicesToRegister[i].getName();
		}
		ServiceRegistration registration = context.registerService(serviceIntfs, device, serviceProperties);

		return registration;
	}
	
}
