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
package fr.liglab.adele.icasa.simulator.impl;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity.State;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity.Relation;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.person.Person;
import fr.liglab.adele.icasa.simulator.person.PersonType;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import java.util.List;

/**
 * Created by Eva on 12/02/2016.
 */
@ContextEntity(services=Person.class)
public class PersonImpl implements Person{

    @State.Field(service = Person.class, state = NAME, directAccess = true)
    private String personName;

    @State.Field(service = Person.class, state = TYPE, directAccess = true)
    private PersonType personType;

    /*TODO : le type n'étant pas spécifié, je me suis basée sur position (pour être en accord avec l'interface)*/
    @State.Field(service = LocatedObject.class, state = OBJECT_X, directAccess = true)
    private int x;

    /*TODO : le type n'étant pas spécifié, je me suis basée sur position (pour être en accord avec l'interface)*/
    @State.Field(service = LocatedObject.class, state = OBJECT_Y, directAccess = true)
    private int y;

    public static final String RELATION_IS_CONTAINED = "is contained";

    @State.Field(service = LocatedObject.class, state = ZONE, directAccess = true)
    private String zone;

    @Relation.Field(value = RELATION_IS_CONTAINED)
    @Requires(id = "zone", specification=Zone.class, optional=true)
    private Zone containingZone;

    @Bind(id = "zone")
    public void bindZone(Zone zone){
        this.zone = zone.getZoneName();
    }

    @Unbind(id = "zone")
    public void unbindZone(Zone zone){
        this.zone = LOCATION_UNKNOWN;
    }

    @Override
    public String getName() {
        return personName;
    }

    @Override
    public void setName(String name) {
        this.personName = name;
    }

    @Override
    public PersonType getPersonType() {
        return personType;
    }

    @Override
    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public Position getPosition() {
        return new Position(x,y);
    }

    @Override
    public void setPosition(Position position) {
        x = position.x;
        y = position.y;
    }
}
