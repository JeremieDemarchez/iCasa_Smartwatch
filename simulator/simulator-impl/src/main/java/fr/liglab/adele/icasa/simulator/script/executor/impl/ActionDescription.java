/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.script.executor.impl;

import org.json.JSONObject;

public class ActionDescription {
	
	private int delay;
	private String commandName;
	private JSONObject configuration;
	
	public ActionDescription(int delay, String commandName, JSONObject configuration) {
	   this.delay = delay;
	   this.commandName = commandName;
	   this.configuration = configuration;
   }

	/**
    * @return the delay
    */
   public int getDelay() {
   	return delay;
   }

	/**
    * @return the commandName
    */
   public String getCommandName() {
   	return commandName;
   }

	/**
    * @return the configuration
    */
   public JSONObject getConfiguration() {
   	return configuration;
   }
   
   @Override
   public String toString() {
   	return "Delay: " + delay  + " - Command Name: " + commandName;
   }
	
	

}
