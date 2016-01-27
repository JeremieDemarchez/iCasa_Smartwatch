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
package fr.liglab.adele.icasa.context.model.example;

import java.util.Set;

/**
 * This service provides all information about application context including available devices and zones. It is the main
 * entry point of the iCasa platform. Most operations on the platform should be made using this service instead of
 * modifying directly the platform objects (Zones, Devices).
 */
public interface LocationManager {

	public final static int ZONE_DEFAULT_Z = 0;

	public final static int ZONE_DEFAULT_Z_LENGHT = 4;

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
	public void createZone(String id, int leftX, int topY, int bottomZ, int width, int height, int depth);

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
	 * Gets the set of zones' IDs.
	 * 
	 * @return The set of zones' IDs.
	 */
	public Set<String> getZoneIds();


}
