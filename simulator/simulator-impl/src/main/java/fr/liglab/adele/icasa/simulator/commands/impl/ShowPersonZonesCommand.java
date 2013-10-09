/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.commands.impl;

import fr.liglab.adele.icasa.commands.Signature;
import fr.liglab.adele.icasa.commands.AbstractCommand;
import fr.liglab.adele.icasa.commands.ScriptLanguage;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

@Component(name = "ShowPersonZonesCommand")
@Provides
@Instantiate(name="person-zones-command")
public class ShowPersonZonesCommand extends AbstractCommand {


    @Requires
    private SimulationManager manager;


    private static final String[] PARAMS =  new String[]{ScriptLanguage.PERSON};

    private static final String NAME= "show-person-zones";

    public ShowPersonZonesCommand(){
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
    public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {

        String personName = param.getString(PARAMS[0]);
        Person person = manager.getPerson(personName);

        if (person != null) {
            List<Zone> zones = manager.getZones();

            out.println("Zones: ");
            for (Zone zone : zones) {
                if (zone.contains(person)) {
                    out.println("Zone : " + zone);
                }
            }
        }
        return null;
    }

    @Override
    public String getDescription(){
        return "Shows the zones containing a person.\n\t" + super.getDescription();
    }

}