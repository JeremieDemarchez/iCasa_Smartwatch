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


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.Zone;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "SetZoneParentCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{setZoneParent}"),
      @StaticServiceProperty(name = "name", value = "set-zone-parent", type = "String") })
@Instantiate(name = "set-parent-zone-command")
public class SetZoneParentCommand extends AbstractCommand {

			
	@Requires
	private SimulationManager simulationManager;

	private String zoneId;

	private String parentId;
	
	private boolean useParentVariables;
	

	@Override
	public Object execute() throws Exception {
		simulationManager.setParentZone(zoneId, parentId);
		Zone zone = simulationManager.getZone(zoneId);
		if (zone!=null)
			zone.setUseParentVariables(useParentVariables);
		return null;
	}
	
	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.zoneId = param.getString("zone");	 
		this.parentId = param.getString("parentZone");
		this.useParentVariables = param.getBoolean("useParentVariables");
	}
	
	
	public void setZoneParent(String zoneId, String parentId, boolean useParentVariables) throws Exception {		
	   this.zoneId = zoneId;
	   this.parentId = parentId;
	   this.useParentVariables = useParentVariables;
	   execute();
   }
	

}