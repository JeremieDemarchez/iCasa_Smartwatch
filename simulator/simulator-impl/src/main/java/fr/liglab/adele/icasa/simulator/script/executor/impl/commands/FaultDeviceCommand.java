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
@Component(name = "FaultDeviceCommand")
@Provides
@Instantiate(name = "fault-device-command")
public class FaultDeviceCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute(JSONObject obj) throws Exception {
		String deviceId = obj.getString(ScriptLanguage.DEVICE_ID);
		simulationManager.setDeviceFault(deviceId, true);
		return null;
	}

	/**
	 * Get the name of the Script and command gogo.
	 * 
	 * @return The command name.
	 */
	@Override
	public String getName() {
		return "fault-device";
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
	public String getDescription() {
		return "Simulate a fail in a device.\n\t" + super.getDescription();
	}
}
