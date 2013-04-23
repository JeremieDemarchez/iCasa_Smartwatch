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
@Component(name = "MovePersonIntoZoneCommand")
@Provides
@Instantiate(name = "move-person-zone-command")
public class MovePersonIntoZoneCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute(JSONObject param) throws Exception {
        String personId = param.getString(ScriptLanguage.PERSON_ID);
        String zoneId = param.getString(ScriptLanguage.ZONE_ID);
        System.out.println("Moving " + personId + " to Zone " + zoneId);
		simulationManager.setPersonZone(personId, zoneId);
		return null;
	}
	
    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "move-person-zone";
    }

    /**
     * Get the list of parameters.
     *
     * @return
     */
    @Override
    public String[] getParameters() {
        return new String[]{ScriptLanguage.PERSON_ID, ScriptLanguage.ZONE_ID};
    }

    @Override
    public String getDescription(){
        return "Move a person into a zone.\n\t" + super.getDescription();
    }
}