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
package fr.liglab.adele.icasa.commands.impl.shell;


import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.Signature;
import fr.liglab.adele.icasa.commands.impl.AbstractCommand;
import fr.liglab.adele.icasa.commands.impl.ScriptLanguage;
import fr.liglab.adele.icasa.location.Zone;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "SetZoneParentCommand")
@Provides
@Instantiate(name = "set-parent-zone-command")
public class SetZoneParentCommand extends AbstractCommand {

	@Requires
	private ContextManager simulationManager;

    private static final String[] PARAMS =  new String[]{ScriptLanguage.ZONE, ScriptLanguage.PARENT_ZONE, ScriptLanguage.USE_PARENT_VARIABLES};

    private static final String NAME= "set-zone-parent";

    public SetZoneParentCommand(){
        addSignature(new Signature(PARAMS));
    }

    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return NAME;
    }



	@Override
	public Object execute(InputStream in, PrintStream out,JSONObject param, Signature signature) throws Exception {
        String zoneId = param.getString(PARAMS[0]);
        String parentId = param.getString(PARAMS[1]);
        boolean useParentVariables = param.getBoolean(PARAMS[2]);
		simulationManager.setParentZone(zoneId, parentId);
		Zone zone = simulationManager.getZone(zoneId);
		if (zone!=null)
			zone.setUseParentVariables(useParentVariables);
		return null;
	}
    @Override
    public String getDescription(){
        return "Set the parent of a zone.\n\t" + super.getDescription();
    }


}