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
package fr.liglab.adele.icasa.simulator.temperature.impl;

import fr.liglab.adele.icasa.clock.Clock;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.clock.ClockListener;

/**
 * System clock used to schedule temperature simulation computation.
 *
 * @author Thomas Leveque
 *
 */

public class SystemClockImpl implements Clock {

    public static final Clock SINGLETON = new SystemClockImpl();

    private long initDate;

    private SystemClockImpl() {
        initDate = System.currentTimeMillis();
    }

    @Override
    public String getId() {
        return "temperaturSimulationSystemClock";
    }

    /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#currentTimeMillis()
    */
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#getFactor()
    */
    public int getFactor() {
        return 1;
    }

    /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setFactor(int)
    */
    public void setFactor(int factor) {
        // do nothing, cannot change factor or system clock
    }

    /* (non-Javadoc)
    * @see fr.liglab.adele.icasa.clock.api.SimulatedClock#setStartDate(long)
    */
    public void setStartDate(long startDate) {
        initDate = startDate;
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public long getElapsedTime() {
        return currentTimeMillis() - initDate;
    }

    public long getStartDate() {
        return initDate;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void addListener(ClockListener listener) {
       // do nothing, clock cannot be modified
    }

    @Override
    public void removeListener(ClockListener listener) {
        // do nothing, clock cannot be modified
    }

}

