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
/**
 * 
 */
package fr.liglab.adele.zigbee.device.importer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.rose.AbstractImporterComponent;
import org.ow2.chameleon.rose.ImporterService;
import org.ow2.chameleon.rose.RoseMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.TypeCode;

/**
 *  rose importer for zigbee presence sensor devices.
 *	@author Kettani Mehdi
 */
@Component(name="zigbee.device.importer")
@Provides(specifications= {ImporterService.class},
properties = {
        @StaticServiceProperty(type = "java.lang.String", name = "rose.protos.configs", value="zigbee") })
public class ZigbeeImporter extends AbstractImporterComponent {

	@Requires(filter="(factory.name=zigbeePresenceSensor)")
	private Factory presenceSensorFactory;
	
	@Requires(filter="(factory.name=zigbeeBinaryLight)")
	private Factory binaryLightFactory;
	
	@Requires(filter="(factory.name=zigbeeMotionSensor)")
	private Factory motionSensorFactory;

    @Requires(filter="(factory.name=zigbeePowerSwitch)")
    private Factory powerSwitchFactory;

    @Requires(filter="(factory.name=zigbeePushButton)")
    private Factory pushButtonFactory;
	
	@Requires(id="rose.machine")
	private RoseMachine roseMachine;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ZigbeeImporter.class);
	
	@Override
	public List<String> getConfigPrefix() {
		List<String> list = new ArrayList<String>();
		list.add("zigbee");
		return list;
	}

	@Override
	public RoseMachine getRoseMachine() {
		return roseMachine;
	}

	@Override
	protected ServiceRegistration createProxy(EndpointDescription epd,
			Map<String, Object> arg1) {
		ComponentInstance instance;
		try {
			
			Factory factory = null;

			if (epd != null){
				Map<String, Object> epdProps = epd.getProperties();
				String deviceType = (String) epdProps.get("zigbee.device.type.code");
				String moduleAddress = (String) epdProps.get("id");
				String serialNumber = (String) epdProps.get(RemoteConstants.ENDPOINT_ID);
				logger.debug("endpoint received in importer with module address : " + moduleAddress);
				
				if (TypeCode.IA001.toString().equals(deviceType)){
					factory = binaryLightFactory;
				} else if (TypeCode.IC003.toString().equals(deviceType)){
					factory = presenceSensorFactory;
				} else if (TypeCode.IC001.toString().equals(deviceType)){
                    factory = pushButtonFactory;
                } else if (TypeCode.IC002.toString().equals(deviceType)){
					factory = powerSwitchFactory;
				} else if (TypeCode.IC004.toString().equals(deviceType)){
                    factory = motionSensorFactory;
                } else {
					// device type not supported
					return null;
				}
				
				Hashtable properties = new Hashtable();
				properties.put("zigbee.moduleAddress", moduleAddress);
				properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, serialNumber);
				
				instance = factory.createComponentInstance(properties);
				logger.debug("proxy created for zigbee device.");
				
				if (instance != null) {
					ServiceRegistration sr = new IpojoServiceRegistration(instance);
					return sr;
				}
			}



		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	protected void destroyProxy(EndpointDescription arg0,
			ServiceRegistration sr) {
		sr.unregister();
	}

	@Override
	protected LogService getLogService() {
		return null;
	}
	
	@Validate
	protected void start() {
		super.start();
	}
	
	@Invalidate
	protected void stop() {
		super.stop();
	}
	
	/**
	 * A wrapper for ipojo Component instances
	 * 
	 * @author Gabriel Pedraza Ferreira
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
				ServiceReference[] references = instance.getContext().getServiceReferences(
				      instance.getClass().getCanonicalName(), "(instance.name=" + instance.getInstanceName() + ")");
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
		 * org.osgi.framework.ServiceRegistration#setProperties(java.util.Dictionary)
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
