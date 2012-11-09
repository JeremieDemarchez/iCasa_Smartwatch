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
package fr.liglab.adele.icasa.script.executor.impl.commands;


import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 *
 */
public class ModifyDeviceValueCommand extends DeviceCommand {

	
	private ConfigurationAdmin configAdmin;
	
	private String variable;
	
	private String value;

	@Override
   public Object execute() throws Exception {
		Configuration config;
      try {
	      
      	System.out.println("Device ID ----------- " + deviceId);
      	config = configAdmin.getConfiguration(deviceId, null);

      	
			Dictionary<String, String> dict = new Hashtable<String, String>();

			dict.put(variable, value);
			dict.put("fault", "yes");

			config.update(dict);
			
			  
      } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }
		return null;
   }

}
