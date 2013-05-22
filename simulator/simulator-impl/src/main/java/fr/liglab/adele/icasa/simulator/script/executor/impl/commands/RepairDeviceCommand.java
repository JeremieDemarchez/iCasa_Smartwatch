/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
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
package fr.liglab.adele.icasa.simulator.script.executor.impl.commands;

import fr.liglab.adele.icasa.commands.impl.AbstractCommand;
import fr.liglab.adele.icasa.commands.impl.ScriptLanguage;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 * 
 */
@Component(name = "RepairDeviceCommand")
@Provides
@Instantiate(name = "repair-device-command")
public class RepairDeviceCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;


	/**
	 * Get the name of the Script and command gogo.
	 * 
	 * @return The command name.
	 */
	@Override
	public String getName() {
		return "repair-device";
	}

	/**
	 * Get the list of parameters.
	 * 
	 * @return
	 */
	@Override
	public String[] getParameters() {
		return new String[] { ScriptLanguage.DEVICE_ID };
	}

	@Override
	public Object execute(JSONObject param) throws Exception {
		String deviceId = param.getString(ScriptLanguage.DEVICE_ID);
		simulationManager.setDeviceFault(deviceId, false);
		return null;
	}

	@Override
	public String getDescription() {
		return "Simulates the device reparation.\n\t" + super.getDescription();
	}

}
