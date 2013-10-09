/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.m2mappbuilder.device.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import fr.liglab.adele.m2mappbuilder.device.generic.MedicalGenericDevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ow2.chameleon.rose.RoseMachine;

@Component(name = "medical.device.rose.discovery", immediate = true)
public class MedicalDeviceDiscovery {

	private BundleContext m_context;

	private final ServiceTracker m_deviceTracker;

	@Requires
	private RoseMachine roseMachine;

	public MedicalDeviceDiscovery(BundleContext context) {
		m_context = context;

		m_deviceTracker = new ServiceTracker(m_context,
				MedicalGenericDevice.class.getName(), new ServiceTrackerCustomizer() {

					public Object addingService(ServiceReference reference) {
						MedicalGenericDevice device = (MedicalGenericDevice) m_context
								.getService(reference);
						
						registerDeviceInROSE(device, reference);

						return device;
					}

					public void modifiedService(ServiceReference reference,
							Object service) {
						// do nothing
					}

					public void removedService(ServiceReference reference,
							Object service) {
						MedicalGenericDevice device = (MedicalGenericDevice) service;
						
						unregisterDeviceInROSE(device);
						
						m_context.ungetService(reference);
					}
				});
	}
	
	@Validate
	public void start() {
		m_deviceTracker.open();
	}

	@Invalidate
	public void stop() {
		m_deviceTracker.close();
	}

	private void unregisterDeviceInROSE(MedicalGenericDevice device) {

		String serialNumber = getROSESerialNumber(device.getSerialNumber());

		roseMachine.removeRemote(serialNumber);
	}

	private void registerDeviceInROSE(MedicalGenericDevice device, ServiceReference reference) {

		Map<String, Object> props = new HashMap<String, Object>();

		String serialNumber = device.getSerialNumber();
		String roseSerialNumber = getROSESerialNumber(serialNumber);

		props.put(RemoteConstants.ENDPOINT_ID, roseSerialNumber);
		props.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "medical.device");
		props.put("serialNumber", serialNumber);
		Object presenseServiceId = reference.getProperty(Constants.SERVICE_ID);
		props.put("sensor.service.id", presenseServiceId);
		
		// compute device interfaces
		Class<?>[] deviceInterfaces = device.getClass().getInterfaces();
		List<String> medicalDeviceInterfaces = new ArrayList<String>();
		for (Class<?> deviceInterface : deviceInterfaces) {
			if (MedicalGenericDevice.class.isAssignableFrom(deviceInterface))
				medicalDeviceInterfaces.add(deviceInterface.getName());
		}
		
		props.put(
				"objectClass",
				medicalDeviceInterfaces.toArray(new String[medicalDeviceInterfaces.size()]));

		EndpointDescription epd = new EndpointDescription(props);
		roseMachine.putRemote(roseSerialNumber, epd);
	}

	private String getROSESerialNumber(String serialNumber) {
		return "medicalDevice#" + serialNumber;
	}
}
