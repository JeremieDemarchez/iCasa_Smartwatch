/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.service.scheduler;

import fr.liglab.adele.icasa.clock.Clock;


/**
 * An OSGi service providing this interface will allow to schedule a task at a specified time using the
 * a provided Clock {@link fr.liglab.adele.icasa.clock.Clock}.
 *
 * @author Thomas Leveque
 */
public interface SpecificClockScheduledRunnable extends ICasaRunnable {

    /**
     * Get the date to be scheduled the iCasa tasks using the iCasa Clock.
     * @return the date in milliseconds.
     */
    public long getExecutionDate();

    /**
     * Returns the clock that must be used for scheduling.
     * @return the clock that must be used for scheduling.
     */
    public Clock getClock();
}
