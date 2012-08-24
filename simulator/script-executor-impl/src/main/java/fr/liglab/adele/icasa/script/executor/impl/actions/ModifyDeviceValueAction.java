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
/**
 * 
 */
package fr.liglab.adele.icasa.script.executor.impl.actions;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import fr.liglab.adele.icasa.script.executor.impl.ScriptExecutorImpl;
import org.osgi.service.cm.Configuration;

/**
 * @author Gabriel
 * 
 */
public class ModifyDeviceValueAction extends DeviceAction {

	private String value;
	private String variable;
	
	
	
	
	public ModifyDeviceValueAction(ScriptExecutorImpl simulatedBehavior, int delay, String deviceId, String variable, String value) {
	   super(simulatedBehavior, delay, deviceId);
	   this.variable = variable;
	   this.value = value;
   }

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.script.executor.impl.actions.Action#run()
	 */
	@Override
	public void run() {
		Configuration config;
      try {
	      
      	System.out.println("Device ID ----------- " + deviceId);
      	config = scriptExecutorImpl.getConfigAdmin().getConfiguration(deviceId, null);
			//Dictionary myDictionary = config.getProperties();
			//System.out.println(myDictionary);
			Dictionary<String, String> dict = new Hashtable<String, String>();

			dict.put(variable, value);
			dict.put("fault", "yes");

			config.update(dict);
			
			System.out.println("Modify Device Value Executed");
			  
      } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }


	}

}
