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
package fr.liglab.adele.icasa.simulation.test.scheduled;

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class PeriodicScheduledTestTask implements PeriodicRunnable {


    private Clock clock;

    private long period;

    private TimeUnit unit;

    public PeriodicScheduledTestTask(Clock clockService, long period,TimeUnit unit){
        clock = clockService;
        this.period = period;
        this.unit = unit;
    }

    /**
     * Gets the scheduled period.
     *
     * @return the period in second.
     */
    public long getPeriod() {
        return period;
    }

    @Override
    public TimeUnit getUnit() {
        return unit;
    }

    public void run() {
        System.out.println("Executing service Simulation date: " + new Date(clock.currentTimeMillis()));
        System.out.println("Executing service Real Date: " + new Date());
    }
}
