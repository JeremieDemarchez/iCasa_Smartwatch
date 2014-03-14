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
package fr.liglab.adele.icasa.simulator.illuminance.impl;

/**
 * Created by aygalinc on 12/03/14.
 */
/**
 * The listener interface for receiving momentOfTheDay events.
 * The class that is interested in processing a momentOfTheDay
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * MomentOfTheDayService <code>register<code> method. When
 * the momentOfTheDay event occurs, that object's appropriate
 * method (<code>momentOfTheDayHasChanged</code>) is invoked.
 *
 * When the listener is leaving, it must unregister.
 *
 */
public interface PartOfTheDayListener {

    /**
     * Notify the listener that moment of the day has changed.
     *
     * @param newPartOfTheDay
     *            the new moment of the day
     */
    void momentOfTheDayHasChanged(PartOfTheDay newPartOfTheDay);
}
