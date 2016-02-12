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

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.impl.ZoneImpl;
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
    public void bindPersons (Person person,Map<String,Object> properties) {
        for (Zone zone : zones) {
            if (! zone.canContains(person.getPosition())) {
                continue;
            }
            personIsContainedCreator.create(person.getName(),zone.getZoneName());
        }
    }

    @Modified(id = "persons")
    public void modifiedPersons (Person person,Map<String,Object> properties) {
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
    public void unbindPersons (Person person,Map<String,Object> properties) {
        personIsContainedCreator.delete(person.getName(),person.getZone());
    }

    /**
     * Add relations person-zone comparing positions
     * @param zone
     */
//    private void addLocationZone(Zone zone){
//        for (Person person: persons){
//            if (zone.canContains(person.getPosition())){
//                /*TODO: Bizarre...*/
//                personIsContainedCreator.create(((ContextEntity)person).getId(),((ContextEntity)zone).getId());
//                zoneContainsCreator.create(((ContextEntity)zone).getId(), ((ContextEntity)person).getId());
//            }
//        }
//    }
//
//    /**
//     * Remove all relations person-zone with zone name
//     * @param zone
//     */
//    private void removeLocationZone(Zone zone){
//        /*TODO: ça serait mieux si CREATOR_RELATION.getInstances() retournait toutes les relations*/
//        /*TODO: sinon ça serait bien aussi de pouvoir accéder à toutes les relations liées à une id*/
//        for (Person person: persons){
//            personIsContainedCreator.delete(((ContextEntity)person).getId(),((ContextEntity)zone).getId());
//            zoneContainsCreator.delete(((ContextEntity)zone).getId(), ((ContextEntity)person).getId());
//        }
//    }
//
//    /**
//     * Add relations person-zone comparing positions
//     * @param person
//     */
//    private void addLocationPerson(Person person){
//        Position personPosition = person.getPosition();
//        for (Zone zone: zones){
//            if (zone.canContains(personPosition)){
//                personIsContainedCreator.create(((ContextEntity)person).getId(),((ContextEntity)zone).getId());
//                zoneContainsCreator.create(((ContextEntity)zone).getId(), ((ContextEntity)person).getId());
//            }
//        }
//    }
//
//    /**
//     * Remove all relations person-zone with person name
//     * @param person
//     */
//    private void removeLocationPerson(Person person){
//        for (Zone zone: zones){
//            personIsContainedCreator.delete(((ContextEntity)person).getId(),((ContextEntity)zone).getId());
//            zoneContainsCreator.delete(((ContextEntity)zone).getId(), ((ContextEntity)person).getId());
//        }
//
//    }

}
