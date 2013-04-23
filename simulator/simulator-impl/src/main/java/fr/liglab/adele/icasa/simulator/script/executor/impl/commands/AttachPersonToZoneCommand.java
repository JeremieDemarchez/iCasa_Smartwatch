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
@Component(name = "AttachPersonZoneCommand")
@Provides
@Instantiate(name = "attach-person-zone-command")
public class AttachPersonToZoneCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute(JSONObject obj) throws Exception {
        String person = obj.getString(ScriptLanguage.PERSON);
        String zone = obj.getString(ScriptLanguage.ZONE);
        boolean attach = obj.getBoolean(ScriptLanguage.ATTACH);
		if (attach)
			simulationManager.attachPersonToZone(zone, person);
		else
			simulationManager.detachPersonFromZone(zone, person);
		return null;
	}
	
	
	@Override
	public boolean validate(JSONObject param) throws Exception {
        return param.has(ScriptLanguage.PERSON) && param.has(ScriptLanguage.ZONE) && param.has(ScriptLanguage.ATTACH);

    }

    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "attach-person-zone";  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get the list of parameters.
     *
     * @return
     */
    @Override
    public String[] getParameters() {
        return new String[]{ScriptLanguage.PERSON, ScriptLanguage.ZONE, ScriptLanguage.ATTACH};  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String getDescription(){
        return "Attach/detach a person to/from a zone.\n\t" + super.getDescription();
    }

}