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
package fr.liglab.adele.icasa.simulation.test.person;

import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;

/**
 * 
 * Used to listen Zone events and track the event information received.
 */
public class PersonTestListener implements PersonListener {


	private Position listenOldPosition = null;

	/**
	 * Invoked when a person has been added to the iCasa Simulator.
	 * 
	 * @param person The added person.
	 */
	public void personAdded(Person person) {
		// To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Invoked when a person has been removed to the iCasa Simulator.
	 * 
	 * @param person The removed peron.
	 */
	public void personRemoved(Person person) {
		// To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Invoked when a person has been moved, to see the new position invoke the <code>getCenterAbsolutePosition()</code>
	 * method.
	 * 
	 * @param person The moved person.
	 * @param oldPosition The last position center absolute position.
	 */
	public void personMoved(Person person, Position oldPosition) {
		listenOldPosition = oldPosition;
	}

	/**
	 * Invoked when a person has been attached to a
	 * 
	 * @param person
	 * @param device
	 */
	public void personDeviceAttached(Person person, LocatedDevice device) {
		// To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * @param person
	 * @param device
	 */
	public void personDeviceDetached(Person person, LocatedDevice device) {
		// To change body of implemented methods use File | Settings | File Templates.
	}

	public Position getOldPosition() {
		return listenOldPosition;
	}

}
