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
import fr.liglab.adele.icasa.service.scheduler.impl.TaskReferenceImpl;

public class OneShotTaskImpl extends TaskReferenceImpl {

    private final Long m_executionTime;

    public OneShotTaskImpl(Clock service,
                           Runnable task,  Long executionTime) {
        super(service,  task) ;
        // If the task is registered after the wanted execution time, of if the
        // execution time is not specified, it is an immediate task.
        long registrationTime = getRegistrationTime();
        if (executionTime == null
                || executionTime.longValue() < registrationTime) {
            m_executionTime = new Long(registrationTime);
        } else {
            m_executionTime = executionTime;
        }
    }

    public Long computeNextExecutionTime(long fromTime) {
        if (fromTime > m_executionTime.longValue()) {
            return null;
        }
        return m_executionTime;
    }

    public Long getNextExecutionTime() {
        return m_executionTime;
    }

}
