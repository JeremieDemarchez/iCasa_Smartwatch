/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research Group
 *   Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.distribution.test;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;

import java.util.Date;


public class PeriodicScheduledTestTask implements PeriodicRunnable {


    private Clock clock;

    public PeriodicScheduledTestTask(Clock clockService){
            clock = clockService;
        }

        /**
         * Gets the scheduled period.
         *
         * @return the period in second.
         */
        public long getPeriod() {
            return 1000;
        }

        /**
         * Gets the job's group.
         * Jobs sharing a group use the same thread pool.
         *
         * @return the job's group
         */
        public String getGroup() {
            return "group1";  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void run() {
            System.out.println("Executing service: " + new Date(clock.currentTimeMillis()));
        }
}
