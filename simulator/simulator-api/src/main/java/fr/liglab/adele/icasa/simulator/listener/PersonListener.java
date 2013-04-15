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
package fr.liglab.adele.icasa.simulator.listener;

import fr.liglab.adele.icasa.listener.IcasaListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.Person;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 30/11/12
 * Time: 15:20
 * The listener interface for receiving person events.
 */
public interface PersonListener extends IcasaListener {
    /**
     * Invoked when a person has been added to the iCasa Simulator.
     * @param person The added person.
     */
    void personAdded(Person person);

    /**
     * Invoked when a person has been removed to the iCasa Simulator.
     * @param person The removed peron.
     */
    void personRemoved(Person person);

    /**
     * Invoked when a person has been moved, to see the new position invoke the
     * <code>getCenterAbsolutePosition()</code> method.
     * @param person The moved person.
     * @param oldPosition The last position center absolute position.
     */
    void personMoved(Person person, Position oldPosition);

    /**
     * Invoked when a person has been attached to a
     * @param person
     * @param device
     */
    void personDeviceAttached(Person person, LocatedDevice device);

    /**
     *
     * @param person
     * @param device
     */
    void personDeviceDetached(Person person, LocatedDevice device);
}
