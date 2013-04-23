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
import fr.liglab.adele.icasa.simulator.script.executor.SimulatorCommand;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

/**
 * 
 * Command to Create a Zone
 * 
 * @author Gabriel
 * 
 */
@Component(name = "CreateZoneCommand")
@Provides(properties = {
      @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
        @StaticServiceProperty(type = "java.lang.String", name = SimulatorCommand.PROP_NAMESPACE, value= SimulatorCommand.DEFAULT_NAMESPACE),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{createZone}"),
      @StaticServiceProperty(name = "description", type = "String", value = "creates a new zone"),
      @StaticServiceProperty(name = "name", value = "create-zone", type = "String") })
@Instantiate(name="create-zone-command")
public class CreateZoneCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String id;
	private int leftX;
	private int topY;
	private int height;
	private int width;

	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute() throws Exception {
		simulationManager.createZone(id, leftX, topY, width, height);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.id = param.getString("id");
		this.leftX = param.getInt("leftX");
		this.topY = param.getInt("topY");
		this.height = param.getInt("height");
		this.width = param.getInt("width");
	}

	public void createZone(String id, int leftX, int topY, int width, int height) throws Exception {
		this.id = id;
		this.leftX = leftX;
		this.topY = topY;
		this.height = height;
		this.width = width;
		execute();
	}

}