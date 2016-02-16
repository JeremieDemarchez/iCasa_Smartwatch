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


import fr.liglab.adele.icasa.commands.AbstractCommand;
import fr.liglab.adele.icasa.commands.ScriptLanguage;
import fr.liglab.adele.icasa.commands.Signature;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.person.Person;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 *
 * Moves a person between the simulated environments 
 *
 *
 */
@Component(name = "MovePersonIntoZoneCommand")
@Provides
@Instantiate(name = "move-person-zone-command")
public class MovePersonIntoZoneCommand extends AbstractCommand {

    @Requires(specification = Person.class,optional = true)
    private List<Person> persons;

    @Requires(specification = Zone.class,optional = true)
    private List<Zone> zones;

    public MovePersonIntoZoneCommand() {
        addSignature(new Signature(new String[]{ScriptLanguage.PERSON_ID, ScriptLanguage.ZONE_ID}));
    }


    @Override
    public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {
        String personId = param.getString(ScriptLanguage.PERSON_ID);
        Person personToMove = null;
        for (Person person : persons){
            if (person.getName().equals(personId)){
                personToMove = person;
                continue;
            }
        }
        if (personToMove == null){
            throw new IllegalArgumentException("Person ("+ personId +") does not exist");
        }
        String zoneId = param.getString(ScriptLanguage.ZONE_ID);
        Zone zoneToMove = null;
        for (Zone zone : zones){
            if (zone.getZoneName().equals(zoneId)){
                zoneToMove = zone;
                continue;
            }
        }
        if (zoneToMove == null){
            throw new IllegalArgumentException("Zone ("+ personId +") does not exist");
        }
        personToMove.setPosition(getRandomPositionIntoZone(zoneToMove));
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

    @Override
    public String getDescription(){
        return "Move a person into a zone.\n\t" + super.getDescription();
    }

    private int random(int min, int max) {
        final double range = (max - 10) - (min + 10);
        if (range <= 0.0) {
            throw new IllegalArgumentException("min >= max");
        }
        return min + (int) (range * Math.random());
    }

    private Position getRandomPositionIntoZone(Zone zone) {
        if (zone == null)
            return null;
        int minX = zone.getLeftTopAbsolutePosition().x;
        int minY = zone.getLeftTopAbsolutePosition().y;
        int newX = random(minX, minX + zone.getXLength());
        int newY = random(minY, minY + zone.getYLength());
        return new Position(newX, newY);
    }
}