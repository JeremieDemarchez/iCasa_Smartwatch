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
package org.medical.script.executor.impl.actions;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.medical.script.executor.impl.ScriptExecutorImpl;
import org.osgi.framework.Constants;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * 
 * Adds an device to available list
 * 
 * @author Gabriel
 * 
 */
public class AddDeviceAction extends DeviceAction {

	private static final Logger logger = LoggerFactory.getLogger(AddDeviceAction.class);

	private String deviceType;

	public AddDeviceAction(ScriptExecutorImpl scriptExecutorImpl, int delay, String deviceId, String deviceType) {
		super(scriptExecutorImpl, delay, deviceId);
		this.scriptExecutorImpl = scriptExecutorImpl;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
	}

	public void run() {
		Factory factory = scriptExecutorImpl.getFactory(deviceType);
		if (factory != null) {
			// Generate a serial number
			Random m_random = new Random();
			String serialNumber = Long.toString(m_random.nextLong(), 16);
			// Create the device
			Dictionary<String, String> properties = new Hashtable<String, String>();
			properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);
			properties.put(GenericDevice.STATE_PROPERTY_NAME, GenericDevice.STATE_ACTIVATED);
			properties.put(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_NO);
			//properties.put(Constants.SERVICE_DESCRIPTION, description);
			properties.put("instance.name", factory.getName() + "-" + serialNumber);
			try {
	         factory.createComponentInstance(properties);
         } catch (UnacceptableConfiguration e) {
	         e.printStackTrace();
         } catch (MissingHandlerException e) {
	         e.printStackTrace();
         } catch (ConfigurationException e) {
	         e.printStackTrace();
         }
		}

	}

	private void registerSensorInROSE(String sensorID) {
		Map<String, Object> props = new Hashtable<String, Object>();

		props.put(RemoteConstants.ENDPOINT_ID, sensorID);
		props.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "simulated-zigbee");
		if (deviceType != null)
			props.put("objectClass", new String[] { deviceType });
		else
			props.put("objectClass", new String[] { "unknowType" });

		props.put("id", sensorID);
		props.put("device.serialNumber", sensorID);

		EndpointDescription epd = new EndpointDescription(props);
		this.scriptExecutorImpl.getRoseMachine().putRemote(sensorID, epd);
		logger.info("Endpoint registed in ROSE");
	}

}