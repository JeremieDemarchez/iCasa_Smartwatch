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
