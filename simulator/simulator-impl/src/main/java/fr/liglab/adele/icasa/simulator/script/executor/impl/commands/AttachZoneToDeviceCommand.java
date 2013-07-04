/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
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


import fr.liglab.adele.icasa.Signature;
import fr.liglab.adele.icasa.commands.impl.AbstractCommand;
import fr.liglab.adele.icasa.commands.impl.ScriptLanguage;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.SimulationManager;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Exception;import java.lang.Object;import java.lang.Override;import java.lang.String;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "AttachZoneToDeviceCommand")
@Provides
@Instantiate(name = "attach-zone-device-command")
public class AttachZoneToDeviceCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

    public AttachZoneToDeviceCommand(){
        addSignature(new Signature(new String[]{ScriptLanguage.DEVICE, ScriptLanguage.ZONE, ScriptLanguage.ATTACH}));
    }

    @Override
	public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {

        String device = param.getString(ScriptLanguage.DEVICE);
        String zone = param.getString(ScriptLanguage.ZONE);
        boolean attach = param.getBoolean(ScriptLanguage.ATTACH);
		if (attach)
			simulationManager.attachZoneToDevice(zone, device);
		else
			simulationManager.detachZoneFromDevice(zone, device);
		return null;
	}
    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "attach-zone-device";
    }


    @Override
    public String getDescription(){
        return "Attach/detach a zone to/from a device.\n\t" + super.getDescription();
    }

}