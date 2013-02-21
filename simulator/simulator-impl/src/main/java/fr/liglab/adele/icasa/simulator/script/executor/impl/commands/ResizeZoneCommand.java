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
package fr.liglab.adele.icasa.simulator.script.executor.impl.commands;

import fr.liglab.adele.icasa.simulator.SimulationManager;
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
@Component(name = "ResizeZoneCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{resizeZone}"),
      @StaticServiceProperty(name = "name", value = "resize-zone", type = "String") })
@Instantiate(name="resize-zone-command")
public class ResizeZoneCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String zoneId;
	private int height;
	private int width;

	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute() throws Exception {
		simulationManager.resizeZone(zoneId, width, height);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.zoneId = param.getString("zoneId");
		this.height = param.getInt("height");
		this.width = param.getInt("width");
	}

	public void resizeZone(String id, int width, int height) throws Exception {
		this.zoneId = id;
		this.height = height;
		this.width = width;
		execute();
	}

}