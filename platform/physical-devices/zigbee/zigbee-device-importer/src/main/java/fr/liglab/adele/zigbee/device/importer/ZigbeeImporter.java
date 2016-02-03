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
/**
 *
 */
package fr.liglab.adele.zigbee.device.importer;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.TypeCode;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.fuchsia.core.component.AbstractImporterComponent;
import org.ow2.chameleon.fuchsia.core.component.ImporterIntrospection;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Fuchsia importer for zigbee devices.
 *
 */
@Component(name = "zigbee.device.importer")
@Provides(specifications = {ImporterService.class, ImporterIntrospection.class})
public class ZigbeeImporter extends AbstractImporterComponent {

	@ServiceProperty(name = ImporterService.TARGET_FILTER_PROPERTY,value ="(protocol=zigbee)")
	private String filter;

	@ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	@Requires(filter = "(factory.name=zigbeePhotometer)")
	private Factory photometerFactory;

	@Requires(filter = "(factory.name=zigbeeBinaryLight)")
	private Factory binaryLightFactory;

	@Requires(filter = "(factory.name=zigbeeMotionSensor)")
	private Factory motionSensorFactory;

	@Requires(filter = "(factory.name=zigbeePowerSwitch)")
	private Factory powerSwitchFactory;

	@Requires(filter = "(factory.name=zigbeePushButton)")
	private Factory pushButtonFactory;

	@Requires(filter = "(factory.name=zigbeeThermometer)")
	private Factory thermometerFactory;

	@Requires(filter = "(factory.name=zigbeePresenceSensor)")
	private Factory presenceSensorFactory;

	private static final Logger logger = LoggerFactory
			.getLogger(ZigbeeImporter.class);

	private final Map<String, ServiceRegistration> zigbeeDevices = new HashMap<String, ServiceRegistration>();

	@Validate
	protected void start() {
		zigbeeDevices.clear();
		super.start();
	}

	@Invalidate
	protected void stop() {
		cleanup();
		super.stop();
	}

	@PostRegistration
	public void registration(ServiceReference serviceReference) {
		super.setServiceReference(serviceReference);
	}


	private void cleanup() {

		for (Map.Entry<String, ServiceRegistration> zigbeeEntry : zigbeeDevices.entrySet()) {
			zigbeeDevices.remove(zigbeeEntry.getKey()).unregister();
		}

	}

	@Override
	protected synchronized void useImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {
		ComponentInstance instance;
		try {

			Factory factory = null;

			if (importDeclaration != null) {
				Map<String, Object> epdProps = importDeclaration.getMetadata();
				String deviceType = (String) epdProps
						.get("zigbee.device.type.code");
				String moduleAddress = (String) epdProps.get("id");
				String serialNumber = (String) epdProps
						.get(RemoteConstants.ENDPOINT_ID);
				logger.debug("endpoint received in importer with module address : "
						+ moduleAddress);

				if (TypeCode.A001.toString().equals(deviceType)) {
					factory = binaryLightFactory;
				} else if (TypeCode.C004.toString().equals(deviceType)) {
					factory = photometerFactory;
				} else if (TypeCode.C001.toString().equals(deviceType)) {
					factory = pushButtonFactory;
				} else if (TypeCode.C002.toString().equals(deviceType)) {
					factory = powerSwitchFactory;
				} else if (TypeCode.C003.toString().equals(deviceType)) {
					factory = motionSensorFactory;
				}  else if (TypeCode.C005.toString().equals(deviceType)) {
					factory = thermometerFactory;
				}else if (TypeCode.C006.toString().equals(deviceType)) {
					factory = presenceSensorFactory;
				} else {
					// device type not supported
					return ;
				}

				Hashtable properties = new Hashtable();
				properties.put("zigbee.moduleAddress", moduleAddress);
				properties
						.put(GenericDevice.DEVICE_SERIAL_NUMBER, serialNumber);

				instance = factory.createComponentInstance(properties);
				logger.debug("proxy created for zigbee device.");

				if (instance != null) {
					ServiceRegistration sr = new IpojoServiceRegistration(
							instance);
					zigbeeDevices.put(serialNumber,sr);
				}
				super.handleImportDeclaration(importDeclaration);

			}

		} catch (Exception ex) {
			logger.error("Error in using import declaration" + importDeclaration.toString(),ex);
		}
	}

	@Override
	protected synchronized void denyImportDeclaration(ImportDeclaration importDeclaration) throws BinderException {
		Map<String, Object> epdProps = importDeclaration.getMetadata();
		String serialNumber = (String) epdProps
				.get(RemoteConstants.ENDPOINT_ID);
		try {
			zigbeeDevices.remove(serialNumber).unregister();
		} catch (IllegalStateException e) {
			logger.error("failed unregistering zigbee device", e);
		}

		unhandleImportDeclaration(importDeclaration);
	}


	@Override
	public String getName() {
		return name;
	}

	/**
	 * A wrapper for ipojo Component instances
	 *
	 *
	 */
	class IpojoServiceRegistration implements ServiceRegistration {

		ComponentInstance instance;

		public IpojoServiceRegistration(ComponentInstance instance) {
			super();
			this.instance = instance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.osgi.framework.ServiceRegistration#getReference()
		 */
		public ServiceReference getReference() {
			try {
				ServiceReference[] references = instance.getContext()
						.getServiceReferences(
								instance.getClass().getCanonicalName(),
								"(instance.name=" + instance.getInstanceName()
										+ ")");
				if (references.length > 0)
					return references[0];
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.framework.ServiceRegistration#setProperties(java.util.Dictionary
		 * )
		 */
		public void setProperties(Dictionary properties) {
			instance.reconfigure(properties);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.osgi.framework.ServiceRegistration#unregister()
		 */
		public void unregister() {
			instance.dispose();
		}

	}

}
