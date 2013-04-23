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
 * Create a new device instance
 * 
 * @author Gabriel
 *
 */
@Component(name = "RemoveDeviceCommand")
@Provides
@Instantiate(name="remove-device-command")
public class RemoveDeviceCommand extends AbstractCommand {

    @Requires
    private SimulationManager simulationManager;


    private static final String[] PARAMS =  new String[]{ScriptLanguage.DEVICE_ID};

    private static final String NAME= "remove-device";

    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Get the list of parameters.
     *
     * @return
     */
    @Override
    public String[] getParameters() {
        return PARAMS;
    }

	@Override
   public Object execute(JSONObject param) throws Exception {
        String deviceId = param.getString(PARAMS[0]);
		simulationManager.removeDevice(deviceId);
		return null;
   }

    @Override
    public String getDescription(){
        return "Remove a simulated device.\n\t" + super.getDescription();
    }



}
