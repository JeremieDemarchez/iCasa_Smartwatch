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

	// -- Zone related methods --//
	
	Zone createZone(String id, String description, int leftX, int topY, int width, int height);
	
	void removeZone(String id);
	
	void moveZone(String id, int leftX, int topY);

	void resizeZone(String id, int width, int height);
	
	Set<String> getZoneVariables(String zoneId);

	Object getZoneVariableValue(String zoneId, String variable);

	void setZoneVariable(String zoneId, String variable, Object value);
	
	List<Zone> getZones();

	Zone getZone(String zoneId);
	
	
	// -- Device related method --//
	
	void setDeviceFault(String deviceId, boolean value);

	void setDeviceState(String deviceId, boolean value);

	void createDevice(String deviceType, String deviceId, Map<String, Object> properties);
	
	List<LocatedDevice> getDevices();
	
	Position getDevicePosition(String deviceSerialNumber);

	void setDevicePosition(String deviceSerialNumber, Position position);

	public void moveDeviceIntoZone(String deviceSerialNumber, String zoneId);

	void removeDevice(String deviceId);

	Set<String> getDeviceTypes();
	
	
	// -- Person related methods -- //
	
	void setPersonPosition(String userName, Position position);

	void setPersonZone(String userName, String environmentId);

	void removeAllPersons();

	void addPerson(String userName);

	void removePerson(String userName);

	public List<Person> getPersons();

	
	// -- Listener related methods -- //

	void addListener(SimulationListener listener);

	void removeListener(SimulationListener listener);
	
}
