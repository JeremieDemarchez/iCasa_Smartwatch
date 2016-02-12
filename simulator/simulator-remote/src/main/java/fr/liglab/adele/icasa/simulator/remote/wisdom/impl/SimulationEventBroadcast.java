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
package fr.liglab.adele.icasa.simulator.remote.wisdom.impl;

import fr.liglab.adele.icasa.remote.wisdom.RemoteEventBroadcast;
import fr.liglab.adele.icasa.simulator.person.Person;
import fr.liglab.adele.icasa.simulator.remote.wisdom.util.IcasaSimulatorJSONUtil;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Instantiate
public class SimulationEventBroadcast  {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimulationEventBroadcast.class);

    @Requires
    private RemoteEventBroadcast myBroadcaster;

    @Bind(id = "persons",optional = true,aggregate=true,specification = Person.class)
    public void bindPerson(Person person) {
        JSONObject json = new JSONObject();
        try {
            json.put("personId", person.getName());
            json.put("person", IcasaSimulatorJSONUtil.getPersonJSON(person));
            myBroadcaster.sendEvent("person-added", json);
        } catch (JSONException e) {
            LOGGER.error("",e);
        }
    }

    @Modified(id = "persons")
    public void modifiedPerson(Person person) {
        JSONObject json = new JSONObject();
        try {
            json.put("personId", person.getName());
            // New position is maybe enough
            json.put("person", IcasaSimulatorJSONUtil.getPersonJSON(person));
            myBroadcaster.sendEvent("person-position-update", json);
        } catch (JSONException e) {
            LOGGER.error("",e);
        }
    }

    @Unbind(id = "persons")
    public void unbindPerson(Person person) {
        JSONObject json = new JSONObject();
        try {
            json.put("personId", person.getName());
            myBroadcaster.sendEvent("person-removed", json);
        } catch (JSONException e) {
            LOGGER.error("",e);
        }
    }

}
