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
package fr.liglab.adele.icasa.service.zone.dimension.calculator;

import fr.liglab.adele.icasa.Variable;

/**
 * The Interface ZoneDimension provides constants for the names of zone
 * dimension variables.
 */
public interface ZoneDimension {

	/** The Variable ZONE_VOLUME_VAR is used to store the zone volume. */
	public final static Variable ZONE_VOLUME_VAR = new Variable("Volume", Double.class,
	        "the volume in m3");

	/** The Variable ZONE_AREA_VAR is used to store the zone area. */
	public final static Variable ZONE_AREA_VAR = new Variable("Area", Double.class,
	        "the volume in m2");

	/**
	 * The Constant ZONE_VOLUME is the name of the Variable corresponding to the
	 * zone volume.
	 */
	public final static String ZONE_VOLUME = ZONE_VOLUME_VAR.getName();

	/**
	 * The Constant ZONE_AREA is the name of the Variable corresponding to the
	 * zone area.
	 */
	public final static String ZONE_AREA = ZONE_AREA_VAR.getName();

}
