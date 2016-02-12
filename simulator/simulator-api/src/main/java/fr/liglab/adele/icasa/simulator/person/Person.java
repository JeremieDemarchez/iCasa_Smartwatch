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
package fr.liglab.adele.icasa.simulator.person;

import fr.liglab.adele.icasa.context.model.annotations.ContextService;
import fr.liglab.adele.icasa.context.model.annotations.State;
import fr.liglab.adele.icasa.location.LocatedObject;

/**
 * This interface represents a person in the icasa simulated Environment
 *
 */
public @ContextService interface Person extends LocatedObject {

	@State String NAME 	= "name";

	@State String TYPE	= "type";

	/**
	 * Gets the person name.
	 * 
	 * @return The person name.
	 */
	public String getName();

	/**
	 * Gets the person type.
	 * 
	 * @return The person type.
	 */
	public PersonType getPersonType();

	/**
	 * Sets the person type.
	 * 
	 * @param personType the new person type.
	 */
	public void setPersonType(PersonType personType);

}
