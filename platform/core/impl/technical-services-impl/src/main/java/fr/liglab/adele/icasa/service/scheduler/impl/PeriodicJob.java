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
package fr.liglab.adele.icasa.service.scheduler.impl;

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.wisdom.api.concurrent.ManagedScheduledFutureTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by aygalinc on 19/11/15.
 */
public class PeriodicJob {

    private final Clock m_clock;

    private ManagedScheduledFutureTask m_futureTask;

    private final PeriodicRunnable m_periodicRunnable;

    public PeriodicJob(PeriodicRunnable runnable,Clock clock) {
        m_periodicRunnable = runnable;
        m_clock = clock;
    }

    public TimeUnit getUnit(){
        return TimeUnit.MILLISECONDS;
    }

    public long getPeriod(){
        return (long)( (double) (m_periodicRunnable.getPeriod()* TimeUtils.getTimeFactorToConvertInMillisecond(m_periodicRunnable.getUnit()))/(double)m_clock.getFactor());
    }

    public void submitted(ManagedScheduledFutureTask futureTask){
        m_futureTask =  futureTask;
    }

    public ManagedScheduledFutureTask task(){
        return m_futureTask;
    }

    public PeriodicRunnable getPeriodicRunnable(){
        return m_periodicRunnable;
    }
}
