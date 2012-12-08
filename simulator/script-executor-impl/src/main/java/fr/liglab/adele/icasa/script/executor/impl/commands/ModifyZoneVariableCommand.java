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

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.script.executor.impl.commands.AbstractCommand;
import fr.liglab.adele.icasa.script.executor.impl.commands.DeviceCommand;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONObject;

/**
 * @author Thomas Leveque
 */
@Component(name = "ModifyZoneVariableCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
        @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{modifyZoneVarValue}"),
        @StaticServiceProperty(name = "name", value = "modify-zone-var", type = "String") })
@Instantiate(name="modify-zone-variable-command")
public class ModifyZoneVariableCommand extends AbstractCommand {

    @Requires
    private SimulationManager simulationManager;

    private String zoneId;
    private String variableName;
    private String newValue;

    @Override
    public Object execute() throws Exception {
        simulationManager.setZoneVariable(zoneId, variableName, newValue);
        return null;
    }

    @Override
    public void configure(JSONObject param) throws Exception {
        this.zoneId = param.getString("zoneId");
        this.variableName = param.getString("variable");
        //TODO manage other value types than String
        this.newValue = param.getString("value");
    }

    public void modifyZoneVarValue(String zoneId, String variableName, String newValue) throws Exception {
        this.zoneId = zoneId;
        this.variableName = variableName;
        this.newValue = newValue;
        execute();
    }

}
