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

import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONObject;

/**
 * @author Thomas Leveque
 */
@Component(name = "ModifyZoneVariableCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{modifyZoneVariableValue}"),
      @StaticServiceProperty(name = "name", value = "modify-zone-variable", type = "String") })
@Instantiate(name = "modify-zone-variable-command")
public class ModifyZoneVariableCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

	private String zoneId;
	private String variableName;
	private String newValue;

	@Override
	public Object execute() throws Exception {
		System.out.println("Modifying variable: " + variableName + " value: " + newValue + " - in Zone: " + zoneId);
		simulationManager.setZoneVariable(zoneId, variableName, new Double(newValue));
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.zoneId = param.getString("zoneId");
		this.variableName = param.getString("variable");
		// TODO manage other value types than String
		this.newValue = param.getString("value");
	}

	public void modifyZoneVariableValue(String zoneId, String variableName, String newValue) throws Exception {
		this.zoneId = zoneId;
		this.variableName = variableName;
		this.newValue = newValue;
		execute();
	}

}
