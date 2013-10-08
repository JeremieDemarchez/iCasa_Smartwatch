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
package fr.liglab.adele.m2mappbuilder.usb.discovery.rose;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.usb.basedriver.descriptor.UsbDevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ow2.chameleon.rose.RoseMachine;


@Component(name="usb.rose.discovery")
public class USBDiscoveryRoseImpl implements ServiceTrackerCustomizer {

	private BundleContext m_context;
	
	private final ServiceTracker m_tracker;
	
	@Requires
   private RoseMachine roseMachine;
   
	
	private Map<ServiceReference, String> usbDevices;
	
	public USBDiscoveryRoseImpl(BundleContext context) {
		m_context = context;
		m_tracker = new ServiceTracker(m_context, UsbDevice.class.getCanonicalName(), this);
	}
	
	@Validate
	public void start() {
		usbDevices = new Hashtable<ServiceReference, String>();
		m_tracker.open();
	}
	
	@Invalidate
	public void stop() {
		m_tracker.close();
		usbDevices = null;
	}
	
	
   private void registerSensorInROSE(String sensorID, ServiceReference reference) {

      Map props = new Properties();
      
      
      props.put(RemoteConstants.ENDPOINT_ID, sensorID);
      props.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "usb");
      props.put("objectClass", new String[] { "someObject" });

      props.put("id", sensorID);
      props.put("device.serialNumber", sensorID);
      Object vendorId = reference.getProperty("org.usb.vendor.id");
      props.put("org.usb.vendor.id", vendorId);
      Object productId = reference.getProperty("org.usb.product.id");
      props.put("org.usb.product.id", productId);


      EndpointDescription epd = new EndpointDescription(props);
      roseMachine.putRemote(sensorID, epd);

   }
	
	@Override
   public Object addingService(ServiceReference reference) {
		Object vendorId = reference.getProperty("org.usb.vendor.id");
		Object productId = reference.getProperty("org.usb.product.id");
		String sensorID = "usb:" + vendorId + ":" + productId + ":" + generateDeviceSerialNumber(); // unable to get tikitag serial number
		usbDevices.put(reference, sensorID);
		registerSensorInROSE(sensorID, reference);
	   return m_context.getService(reference);
   }

	@Override
   public void modifiedService(ServiceReference reference, Object service) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void removedService(ServiceReference reference, Object service) {
		//byte serial = (Byte) reference.getProperty(UsbDevice.SERIAL);
		String sensorID = usbDevices.get(reference);
		roseMachine.removeRemote(sensorID);	   
   }

	private String generateDeviceSerialNumber() {
      Random randomGenerator = new Random();      
      int randomInt = randomGenerator.nextInt(10000);
      return randomInt+"";
	}
	
}
