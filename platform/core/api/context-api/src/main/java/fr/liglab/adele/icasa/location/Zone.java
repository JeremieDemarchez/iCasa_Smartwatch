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
package fr.liglab.adele.icasa.location;

import fr.liglab.adele.icasa.context.annotation.EntityType;

/**
 * This interface represents a Zone (a rectangular surface) into the iCasa platform. Zones can be embeded, a zone can
 * have several children zones. Each children zone must fit into its parent zone.
 *
 *
 */
@EntityType(states = {"zone.name","zone.x","zone.y","zone.z","zone.x.lenght","zone.y.lenght","zone.z.lenght"})
public interface Zone {

	public final static String ZONE_NAME = "zone.name";
	public final static String ZONE_X ="zone.x";
	public final static String ZONE_Y ="zone.y";
	public final static String ZONE_Z ="zone.z";
	public final static String ZONE_X_LENGHT ="zone.x.lenght";
	public final static String ZONE_Y_LENGHT ="zone.y.lenght";
	public final static String ZONE_Z_LENGHT ="zone.z.lenght";

	/**
	 * Returns the zone id.
	 *
	 * @return zone id.
	 */
	public String getZoneName();

	/**
	 * Gets the absolute (x,y) point in the left-top corner of the zone.
	 *
	 * @return the absolute (x,y) point in the left-top corner of the zone.
	 */
	public void setLeftTopAbsolutePosition(Position position);

	/**
	 * Gets the absolute (x,y) point in the left-top corner of the zone.
	 *
	 * @return the absolute (x,y) point in the left-top corner of the zone.
	 */
	public Position getLeftTopAbsolutePosition();

	/**
	 * Gets the absolute (x,y) point in the right-bottom corner of the zone.
	 *
	 * @return the absolute (x,y) point in the right-bottom corner of the zone.
	 */
	public Position getRightBottomAbsolutePosition();

	/**
	 * Gets the zone X length (width)
	 *
	 * @return the zone width
	 */
	public int getXLength();

	/**
	 * Sets the zone X length .
	 *
	 * @param length the new zone X length.
	 * @throws Exception When the zone does not fit its parent zone.
	 */
	public void setXLength(int length) throws Exception;

	/**
	 * Gets the zone Y length.
	 *
	 * @return the zone Y length.
	 */
	public int getYLength();

	/**
	 * Sets the zone Y length.
	 *
	 * @param length the new zone Y length.
	 * @throws Exception When the zone does not fit its parent zone.
	 */
	public void setYLength(int length) throws Exception;
	/**
	 * Gets the zone Z length.
	 *
	 * @return the zone Y length.
	 */
	public int getZLength();

	/**
	 * Sets the zone Y length.
	 *
	 * @param length the new zone Z length.
	 * @throws Exception When the zone does not fit its parent zone.
	 */
	public void setZLength(int length) throws Exception;

	/**
	 * Returns true if a point is geographically contained into the zone.
	 *
	 * @param position a point
	 * @return true if a point is geographically contained into the zone, false if it is not.
	 */
	public boolean canContains(Position position);

	/**
	 * Resizes the zone
	 *
	 * @param newWidth The new width (X length value)
	 * @param newHeight The new Height (Y length value)
	 * @param newDepth The new Depth (Z length value)
	 * @throws Exception When the zone does not fit its parent zone.
	 */
	public void resize(int newWidth, int newHeight, int newDepth) throws Exception;

}
