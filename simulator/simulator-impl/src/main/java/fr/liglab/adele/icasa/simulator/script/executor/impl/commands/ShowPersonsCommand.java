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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import fr.liglab.adele.icasa.Signature;
import fr.liglab.adele.icasa.commands.impl.AbstractCommand;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.json.JSONObject;

@Component
@Provides
@Instantiate
public class ShowPersonsCommand extends AbstractCommand {

	@Requires
	private SimulationManager manager;

    private static final String NAME= "show-persons";

    public ShowPersonsCommand(){
        addSignature(EMPTY_SIGNATURE);
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
        out.println("Persons: ");
        List<Person> persons = manager.getPersons();
        for (Person person : persons) {
            out.println("Person " + person);
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String getDescription(){
        return "Shows the list of persons.\n\t" + super.getDescription();
    }
}
