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
package fr.liglab.adele.icasa.simulator.provider;

import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.impl.PersonImpl;
import fr.liglab.adele.icasa.simulator.person.Person;
import org.apache.felix.ipojo.annotations.*;

import java.util.List;
import java.util.Map;

@Component(immediate = true, publicFactory = false)
@Instantiate
public class PersonLocationManager {

    @Creator.Field(PersonImpl.RELATION_IS_CONTAINED) Creator.Relation<PersonImpl,Zone> personIsContainedCreator;

    @Requires(id = "zones", specification = Zone.class, optional = true)
    List<Zone> zones;

    @Requires(id = "persons", specification = Person.class, optional = true)
    List<Person> persons;

    @Bind(id = "persons")
    public synchronized void bindPersons (Person person,Map<String,Object> properties) {
        for (Zone zone : zones) {
            if (! zone.canContains(person.getPosition())) {
                continue;
            }
            try{
                personIsContainedCreator.create(person.getName(),zone.getZoneName());
            }catch (IllegalArgumentException e){

            }
        }
    }

    @Modified(id = "persons")
    public synchronized void modifiedPersons (Person person,Map<String,Object> properties) {
        for (Zone zone : zones) {
            if (zone.canContains(person.getPosition())) {
                try{
                    personIsContainedCreator.create(person.getName(),zone.getZoneName());
                }catch (IllegalArgumentException e){

                }
            }else {
                personIsContainedCreator.delete(person.getName(),zone.getZoneName());
            }
        }
    }

    @Unbind(id = "persons")
    public synchronized void unbindPersons (Person person,Map<String,Object> properties) {
        personIsContainedCreator.delete(person.getName(),person.getZone());
    }

    @Bind(id = "zones")
    public synchronized void bindZone(Zone zone){
        for (Person person : persons){
            if (zone.canContains(person.getPosition())){
                personIsContainedCreator.create(person.getName(),zone);
            }
        }
    }

    @Modified(id = "zones")
    public synchronized void modifiedZone(Zone zone){
        for (Person person : persons){
            if (zone.canContains(person.getPosition())){
                try {
                    personIsContainedCreator.create(person.getName(),zone);
                }catch (IllegalArgumentException e){

                }
            }else {
                personIsContainedCreator.delete(person.getName(),zone);
            }
        }
    }

    @Unbind(id = "zones")
    public synchronized void unbindZone(Zone zone){
        for (Person person : persons){
            personIsContainedCreator.delete(person.getName(),zone);
        }
    }
}
