/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
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
package fr.liglab.adele.icasa.simulator.listener;

import fr.liglab.adele.icasa.listener.IcasaListener;
import fr.liglab.adele.icasa.simulator.PersonType;

/**
 * Listener interface on {@link fr.liglab.adele.icasa.simulator.PersonType}
 * 
 * @author Thomas Leveque
 */
public interface PersonTypeListener extends IcasaListener {

	/**
	 * Invoked when a person type has been added to the iCasa Simulator.
	 * 
	 * @param personType The added person type.
	 */
	public void personTypeAdded(PersonType personType);

	/**
	 * Invoked when a person type has been removed to the iCasa Simulator.
	 * 
	 * @param personType The removed person type.
	 */
	public void personTypeRemoved(PersonType personType);
}