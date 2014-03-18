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
 * The MomentOfTheDay service is used to retrieve the moment of the day.
 * It also supports listeners that are notified when the moment of the day
 * change.
 */
public interface PartOfTheDayService {

    /**
     * Gets the moment of the day.
     *
     * @return the moment of the day
     */
    PartOfTheDay getMomentOfTheDay();

    /**
     * Register a listener that will be notified each time the current moment of the day
     * changed.
     *
     * @param listener
     *            the listener
     */
    void register(PartOfTheDayListener listener);

    /**
     * Unregister a moment of the day listener.
     *
     * @param listener
     *            the listener
     */
    void unregister(PartOfTheDayListener listener);

}
