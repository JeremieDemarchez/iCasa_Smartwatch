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

import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONObject;

/**
 * @author Thomas Leveque
 */
@Component(name = "AddZoneVariableCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
        @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{addZoneVariable}"),
        @StaticServiceProperty(name = "name", value = "add-zone-variable", type = "String") })
@Instantiate(name="add-zone-variable-command")
public class AddZoneVariableCommand extends AbstractCommand {

    @Requires
    private SimulationManager simulationManager;

    private String zoneId;
    private String variableName;

    @Override
    public Object execute() throws Exception {
        simulationManager.addZoneVariable(zoneId, variableName);
        return null;
    }

    @Override
    public void configure(JSONObject param) throws Exception {
        this.zoneId = param.getString("zoneId");
        this.variableName = param.getString("variable");
    }

    public void addZoneVariable(String zoneId, String variableName) throws Exception {
        this.zoneId = zoneId;
        this.variableName = variableName;
        execute();
    }

}