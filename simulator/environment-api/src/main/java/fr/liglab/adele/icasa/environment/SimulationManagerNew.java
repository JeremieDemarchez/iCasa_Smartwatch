/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.environment;

import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * TODO Comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public interface SimulationManagerNew {

    public String LOCATION_PROP_NAME = "location";

	// -- Zone related methods --//

    public Zone createZone(String id, String description, int leftX, int topY, int width, int height);

    public void removeZone(String id);

    public void moveZone(String id, int leftX, int topY);

    public void resizeZone(String id, int width, int height);

    public Set<String> getZoneVariables(String zoneId);

    public Object getZoneVariableValue(String zoneId, String variable);

    public void setZoneVariable(String zoneId, String variable, Object value);

    public Set<String> getZoneIds();

    public List<Zone> getZones();

    public Zone getZone(String zoneId);

    public Zone getZoneFromPosition(Position position);
	
	
	// -- Device related method --//

    public Set<String> getDeviceIds();

    public void setDeviceFault(String deviceId, boolean value);

    public void setDeviceState(String deviceId, boolean value);

    public void createDevice(String deviceType, String deviceId, Map<String, Object> properties);

    public LocatedDevice getDevice(String deviceId);

    public List<LocatedDevice> getDevices();

    public Position getDevicePosition(String deviceSerialNumber);

    public void setDevicePosition(String deviceSerialNumber, Position position);

	public void moveDeviceIntoZone(String deviceSerialNumber, String zoneId);

    public void removeDevice(String deviceId);

    public Set<String> getDeviceTypes();
	
	
	// -- Person related methods -- //

    public void setPersonPosition(String personName, Position position);

    public void setPersonZone(String personName, String environmentId);

    public void removeAllPersons();

    public void addPerson(String personName);

    public Person getPerson(String personName);

    public void removePerson(String personName);

	public List<Person> getPersons();

	
	// -- Listener related methods -- //

    public void addListener(SimulationListener listener);

    public void removeListener(SimulationListener listener);
	
}
