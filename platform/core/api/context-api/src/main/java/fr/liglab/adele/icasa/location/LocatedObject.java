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

import fr.liglab.adele.cream.annotations.ContextService;
import fr.liglab.adele.cream.annotations.State;

/**
 * This interface represents a object that can be placed using a point into a coordinate system.
 *
 * 
 */
public @ContextService interface LocatedObject {

	public static final String LOCATION_UNKNOWN = "unknown";

	public static final @State String OBJECT_X = "object.position.x";

	public static final @State String OBJECT_Y = "object.position.y";

	public static final @State String ZONE = "object.zone";

	public String getZone();

	public Position getPosition();

	public void setPosition(Position position);
	
}
