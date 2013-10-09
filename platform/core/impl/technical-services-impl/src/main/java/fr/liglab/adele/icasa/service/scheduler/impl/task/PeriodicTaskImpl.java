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
package fr.liglab.adele.icasa.service.scheduler.impl.task;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.ICasaRunnable;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.service.scheduler.impl.TaskReferenceImpl;


public class PeriodicTaskImpl extends TaskReferenceImpl {

    private final Long period;

    private Long executionTime;

    public PeriodicTaskImpl(Clock service,
                           ICasaRunnable task,  Long periodTime) {
        super(service,  task) ;

        period = periodTime;

        executionTime = getRegistrationTime() + periodTime;
    }

    public Long computeNextExecutionTime(long fromTime) {
        if (fromTime > executionTime + period) {
            executionTime = fromTime + 1;
        } else {
            executionTime = fromTime + period;
        }

        return executionTime;
    }

    public Long getNextExecutionTime(){
        return executionTime;
    }

}
