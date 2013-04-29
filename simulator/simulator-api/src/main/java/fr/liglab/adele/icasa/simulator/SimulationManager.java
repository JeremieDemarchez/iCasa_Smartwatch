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
 * TODO Comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public interface SimulationManager extends ContextManager {

    /**
     * Simulates a fail/reparation in a device.
     * @param deviceId The device to manipulate.
     * @param value <code>true</code> to simulate a fail. <code>false</code>to repair.
     */
	public void setDeviceFault(String deviceId, boolean value);

    /**
     * Creates an instance of a simulated device.
     * @param deviceType The device type
     * @param deviceId  The new device identifier.
     * @param properties The properties for the new simulated device.
     * @return The device wrapper object.
     */
	public LocatedDevice createDevice(String deviceType, String deviceId, Map<String, Object> properties);

	public void removeDevice(String deviceId);

	public Set<String> getSimulatedDeviceTypes();
	
	public void removeAllDevices();

	
	
	// -- Person related methods -- //

	public void setPersonPosition(String personName, Position position);

	public void setPersonZone(String personName, String zoneId);

	public void removeAllPersons();

	public Person addPerson(String personName, String personType);

	public Person getPerson(String personName);

	public void removePerson(String personName);

	public List<Person> getPersons();
	
	public void addPersonType(String personType);

	public String getPersonType(String personType);

	public void removePersonType(String personType);

	public List<String> getPersonTypes();
	
	
	// -- Attachements methods -- //
	
	public void attachZoneToDevice(String zoneId, String deviceId);
	
	public void detachZoneFromDevice(String zoneId, String deviceId);
	
	public void attachDeviceToPerson(String deviceId, String personId);
	
	public void detachDeviceFromPerson(String deviceId, String personId);
	
	public void attachPersonToZone(String personId, String zoneId);
	
	public void detachPersonFromZone(String personId, String zoneId);
	
	public void attachDeviceToZone(String deviceId, String zoneId);
	
	public void detachDeviceFromZone(String deviceId, String zoneId);
	
	//public void saveSimulationState(String fileName);

}
