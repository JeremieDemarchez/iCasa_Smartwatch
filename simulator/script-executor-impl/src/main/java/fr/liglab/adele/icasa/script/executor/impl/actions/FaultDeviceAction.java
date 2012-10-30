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

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.script.executor.impl.ScriptExecutorImpl;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 *
 */
public class FaultDeviceAction extends DeviceAction {

	private static final Logger logger = LoggerFactory.getLogger(FaultDeviceAction.class);
	
	public FaultDeviceAction(ScriptExecutorImpl simulatedBehavior, int delay) {
		super(simulatedBehavior, delay);
	}

	@Override
	public void run() {
		GenericDevice device = scriptExecutorImpl.getDevices().get(deviceId);
		if (device != null) {
			//PresenceSensor sensor = (PresenceSensor) device;
			device.setFault("yes");
			logger.info("Device fault state has been set to Yes");
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
	}

}
