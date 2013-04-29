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
package fr.liglab.adele.icasa.simulator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;

/**
 * The <SimulationM
 *
 * @author Gabriel Pedraza Ferreira
 */
public interface SimulationManager extends ContextManager {

    /**
     * Simulates a fail/reparation in a device.
     *
     * @param deviceId The device to manipulate.
     * @param value    <code>true</code> to simulate a fail. <code>false</code>to repair.
     */
    public void setDeviceFault(String deviceId, boolean value);

    /**
     * Creates an instance of a simulated device.
     *
     * @param deviceType The device type
     * @param deviceId   The new device identifier.
     * @param properties The properties for the new simulated device.
     * @return The device wrapper object.
     * @see fr.liglab.adele.icasa.location.LocatedDevice
     */
    public LocatedDevice createDevice(String deviceType, String deviceId, Map<String, Object> properties);

    /**
     * Removes a simulated device instance.
     *
     * @param deviceId The identifier of the simulated device.
     */
    public void removeDevice(String deviceId);

    /**
     * Get a non-modifiable set of the device types available in the iCasa context.
     * The device type corresponds to the component factory name.
     *
     * @return a set of the device types.
     */
    public Set<String> getSimulatedDeviceTypes();

    /**
     * Removes all simulated device instance in iCasa.
     */
    public void removeAllDevices();

    // -- Person related methods -- //

    /**
     * Change the position of a given person.
     *
     * @param personName the name of the person to move.
     * @param position   the new position center absolute position
     * @see fr.liglab.adele.icasa.simulator.Person#getCenterAbsolutePosition()
     */
    public void setPersonPosition(String personName, Position position);

    /**
     * Moves a person to a zone, into a random generated position inside the given zone.
     *
     * @param personName the name of the person.
     * @param zoneId     the zone identifier.
     */
    public void setPersonZone(String personName, String zoneId);

    /**
     * Removes all simulated persons in iCasa.
     */
    public void removeAllPersons();

    /**
     * Creates an instance of a new simulated person.
     * There are six person types available [Grandfather, Grandmother,Father, Mother, Boy, Girl].
     *
     * @param personName the new person name.
     * @param personType the person type
     * @return the created person.
     * @see SimulationManager#addPersonType(String)
     */
    public Person addPerson(String personName, String personType);

    /**
     * Get a person instance currently available in iCasa simulator.
     *
     * @param personName the person to retrieve.
     * @return the <code>Person</code> object instance. <code>null</code> if does not exist.
     */
    public Person getPerson(String personName);

    /**
     * Removes a person currently available in iCasa simulator.
     *
     * @param personName
     */
    public void removePerson(String personName);

    /**
     * Retrieves the list of tha available person in the iCasa simulator.
     *
     * @return an unmodifiable list of persons.
     */
    public List<Person> getPersons();

    /**
     * Add a new person type to the simulation only if it not exist. The call of this method will
     * trigger a personTypeAdded event only it is not already added.
     *
     *
     * @param personType the new person type name.
     * @see fr.liglab.adele.icasa.simulator.listener.PersonTypeListener#personTypeAdded(PersonType) .
     */
    public PersonType addPersonType(String personType);

    /**
     * Get the person type if exist.
     *
     * @param personType the person type name.
     * @return the personType if exist, <code>null</code> if not.
     */
    public PersonType getPersonType(String personType);

    /**
     * Removes a person type and all created persons having the removed type.
     * @param personType the person type name to remove.
     */
    public void removePersonType(String personType);

    /**
     * Get an non-modifiable list of person types available.
     * @return
     */
    public List<PersonType> getPersonTypes();


    // -- Attachements methods -- //

    /**
     * Attach a zone to a device. When moving the device, the zone will be also moved.
     * @param zoneId the identifier of the zone.
     * @param deviceId the identifier of the device.
     */
    public void attachZoneToDevice(String zoneId, String deviceId);

    /**
     * Detach a zone from the device.
     * @param zoneId the zone identifier.
     * @param deviceId
     */
    public void detachZoneFromDevice(String zoneId, String deviceId);

    public void attachDeviceToPerson(String deviceId, String personId);

    public void detachDeviceFromPerson(String deviceId, String personId);

    public void attachPersonToZone(String personId, String zoneId);

    public void detachPersonFromZone(String personId, String zoneId);

    public void attachDeviceToZone(String deviceId, String zoneId);

    public void detachDeviceFromZone(String deviceId, String zoneId);

    //public void saveSimulationState(String fileName);

}
