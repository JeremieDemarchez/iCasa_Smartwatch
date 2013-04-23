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
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "AttachDeviceToPersonCommand")
@Provides
@Instantiate(name = "attach-device-person-command")
public class AttachDeviceToPersonCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "attach-device-person";  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get the list of parameters.
     *
     * @return
     */
    @Override
    public String[] getParameters() {
        return new String[]{ScriptLanguage.PERSON, ScriptLanguage.DEVICE, ScriptLanguage.ATTACH};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object execute(JSONObject param) throws Exception {
        String person = param.getString(ScriptLanguage.PERSON);
        String device = param.getString(ScriptLanguage.DEVICE);
        boolean attach = param.getBoolean(ScriptLanguage.ATTACH);
        if (attach)
            simulationManager.attachDeviceToPerson(device, person);
        else
            simulationManager.detachDeviceFromPerson(device, person);
        return null;
    }


    @Override
    public String getDescription(){
        return "Attach or detach a device to a person.\n\t" + super.getDescription();
    }
}