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
package fr.liglab.adele.icasa.script.executor.impl.actions;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.json.JSONObject;

import fr.liglab.adele.icasa.script.executor.impl.ScriptExecutorImpl;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * 
 * Adds an device to available list
 * 
 * @author Gabriel
 * 
 */
public class AddDeviceAction extends DeviceAction {


	private String deviceType;

	public AddDeviceAction(ScriptExecutorImpl scriptExecutorImpl, int delay) {
		super(scriptExecutorImpl, delay);
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

	@Override
   public Object execute(InputStream in, OutputStream out, JSONObject param) throws Exception {
		configure(param);
		run();
	   return null;
   }

	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.deviceId = param.getString("deviceId");
		this.deviceType = param.getString("deviceType");
	}

}