/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa;

import java.util.List;
import java.util.Set;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.listener.IcasaListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;

/**
 * This service provides all information about application context including available devices and zones. It is the main
 * entry point of the iCasa platform. Most operations on the platform should be made using this service instead of
 * modifying directly the platform objects (Zones, Devices).
 */
public interface ContextManager {

	// -- Zone related methods --//

	/**
	 * Creates a new rectangular zone.
	 * 
	 * @param id the identifier of the zone.
	 * @param leftX the left X value of the zone.
	 * @param topY the top X value of the zone.
	 * @param width the width of the zone.
	 * @param height the height of the zone.
	 * @return the created zone.
	 * @throws IllegalArgumentException when exists a zone with the same identifier.
	 */
	public Zone createZone(String id, int leftX, int topY, int bottomZ, int width, int height, int depth);

	/**
	 * Creates a new rectangular zone using a center position.
	 * 
	 * @param id The identifier of the zone.
	 * @param center The center position of the new zone.
	 * @param detectionScope used to calculate the width/height of the zone. width = detectionScope * 2 height =
	 *           detectionScope * 2
	 * @return the created zone.
	 * @throws IllegalArgumentException when a zone exists with the same identifier.
	 */
	public Zone createZone(String id, Position center, int detectionScope);

	/**
	 * Removes a zone given his identifier. Do nothing if such zone does not exist.
	 * 
	 * @param id The zone identifier.
	 */
	public void removeZone(String id);

	/**
	 * Moves a zone to a new top left corner position.
	 * 
	 * @param id The identifier of the zone to move.
	 * @param leftX The new X corner value.
	 * @param topY The new Y corner value
	 * @throws Exception when new position does not fit in the parent zone.
	 */
	public void moveZone(String id, int leftX, int topY, int bottomZ) throws Exception;

	/**
	 * Resizes a zone using a new width and height.
	 * 
	 * @param id The identifier of the zone to resize.
	 * @param width the new width of the zone.
	 * @param height The new height of the zone
     * @param depth The new depth of the zone
	 * @throws Exception Throws an exception when the zone does not fit in the parent zone.
	 */
	public void resizeZone(String id, int width, int height, int depth) throws Exception;

	/**
	 * Removes all zones in the platform.
	 */
	public void removeAllZones();

	/**
	 * Gets the set of available variables names in a given zone.
	 * 
	 * @param zoneId the zone identifier.
	 * @return The set of the variables names of the zone.
	 */
	public Set<String> getZoneVariables(String zoneId);

	/**
	 * Gets the value of a given variable in a specified zone.
	 * 
	 * @param zoneId The zone identifier to inspect the variables.
	 * @param variableName The variable name to retrieve its value.
	 * @return The value of the variable in the given zone.
	 */
	public Object getZoneVariableValue(String zoneId, String variableName);

	/**
	 * Adds a variable on specified zone. Do nothing if such variable exists.
	 * 
	 * @param zoneId zone identifier
	 * @param variableName name of a variable
	 */
	public void addZoneVariable(String zoneId, String variableName);

	/**
	 * Sets the value of the specified variable.
	 * 
	 * @param zoneId The zone identifier to inspect the variables.
	 * @param variableName The name of the variable to be modified.
	 * @param value The new value of the variable.
	 */
	public void setZoneVariable(String zoneId, String variableName, Object value);

	/**
	 * Gets the set of zones' IDs.
	 * 
	 * @return The set of zones' IDs.
	 */
	public Set<String> getZoneIds();

	/**
	 * Returns all defined zones. If there is no zone then returns an empty list.
	 * 
	 * @return all defined zones.
	 */
	public List<Zone> getZones();

	/**
	 * Gets the zone having the specified ID or null if there is no zone with this ID.
	 * 
	 * @param zoneId The ID of the zone to be retrieved.
	 * @return The zone having the specified ID or null.
	 */
	public Zone getZone(String zoneId);

	/**
	 * Gets the zone containing this position. If several zones contains this positions returns the highest one.
	 * 
	 * @param position A position
	 * @return The zone containing this position.
	 */
	public Zone getZoneFromPosition(Position position);

	/**
	 * Sets the zone parent.
	 * 
	 * @param zoneId The ID of zone to be modified.
	 * @param parentId The ID of the new parent zone.
	 * @throws Exception when the zone does not fit in the new parent zone.
	 */
	public void setParentZone(String zoneId, String parentId) throws Exception;

	// -- Device related method --//

	/**
	 * Returns serial numbers of all available devices.
	 * 
	 * @return serial numbers of all available devices.
	 */
	public Set<String> getDeviceIds();

	/**
	 * Sets activated flag of a device. If activatedFlag is equals to false, applications cannot use the corresponding
	 * device.
	 * 
	 * @param deviceId a device identifier
	 * @param activatedFlag activation flag value
	 */
	public void setDeviceState(String deviceId, boolean activatedFlag);

	/**
	 * Returns an object which represents the device with specified id. It may contain additional information than the
	 * device provides such as location.
	 * 
	 * @param deviceId a device serial number
	 * @return an object which represents the device with specified id.
	 */
	public LocatedDevice getDevice(String deviceId);

	/**
	 * Returns device object that corresponds to specified serial number.
	 * 
	 * @param deviceId device serial number
	 * @return device object that corresponds to specified serial number.
	 */
	public GenericDevice getGenericDevice(String deviceId);

	/**
	 * Returns a set of objects that represent all available devices. If there is no devices, return an empty collection.
	 * 
	 * @return a set of objects that represent all available devices.
	 */
	public List<LocatedDevice> getDevices();

	/**
	 * Returns current position of specified device.
	 * 
	 * @param deviceId a device identifier
	 * @return current position of specified device.
	 */
	public Position getDevicePosition(String deviceId);

	/**
	 * Sets the device position.
	 * 
	 * @param deviceId The ID of device to be moved.
	 * @param position The new position of device.
	 */
	public void setDevicePosition(String deviceId, Position position);

	/**
	 * Moves a device to a zone. The device position is calculated randomly in the zone. 
	 * 
	 * @param deviceId The ID of device to be moved. 
	 * @param zoneId The ID of the zone where the device wll be placed.
	 */
	public void moveDeviceIntoZone(String deviceId, String zoneId);

	/**
	 * Gets the set of devices Types existing in the platform.
	 * 
	 * @return the set of devices Types existing in the platform.
	 */
	public Set<String> getDeviceTypes();

	/**
	 * Get a set of the Service specification
	 * 
	 * @param deviceType the device type to retrieve its service specifications.
	 * @return the set of specifications, null if any.
	 */
	public Set<String> getProvidedServices(String deviceType);

	/**
	 * Returns all global variables.
	 * 
	 * @return all global variables.
	 */
	public Set<Variable> getGlobalVariables();

	/**
	 * Returns the value of a given global variable.
	 * 
	 * @param variableName The variable name to retrieve its value.
	 * @return The value of the variable.
	 */
	public Object getGlobalVariableValue(String variableName);

	/**
	 * Adds a global variable.
	 * 
	 * @param variableName name of a global variable
	 */
	public void addGlobalVariable(String variableName);

	/**
	 * Sets specified global variable .
	 * 
	 * @param variableName
	 * @param value
	 */
	public void setGlobalVariable(String variableName, Object value);

	// -- Listener related methods -- //

	/**
	 * Adds a listener to the platform. The platform determines the type of listener to know where to plug it.
	 * @param listener The new listener.
	 */
	public void addListener(IcasaListener listener);

	/**
	 * Removes a listener from the platform.
	 * @param listener
	 */
	public void removeListener(IcasaListener listener);

	/**
	 * Removes all zones. If the platform is under simulation, also removes simulated persons and simulated devices.
	 */
	public void resetContext();

}
