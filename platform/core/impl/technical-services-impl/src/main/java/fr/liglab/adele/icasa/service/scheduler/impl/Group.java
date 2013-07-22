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
package fr.liglab.adele.icasa.service.scheduler.impl;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.*;
import fr.liglab.adele.icasa.service.scheduler.impl.task.OneShotTaskImpl;
import fr.liglab.adele.icasa.service.scheduler.impl.task.PeriodicTaskImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 *
 */
public class Group  {

    private final String name;
    Map<ICasaRunnable, TaskReferenceImpl> jobs = new HashMap<ICasaRunnable, TaskReferenceImpl>();
    private SchedulerThreadPoolImpl executor;
    private int poolSize;
    private Logger logger;
    private Clock clock;


    public Group(SchedulingManagerImpl.GroupConfiguration config, Clock clock) {
        this.name = config.getName();
        this.clock = clock;
        this.poolSize = config.getMaxThread();
        this.logger = LoggerFactory.getLogger(Group.class.getName() + "-" + this.name);
        executor = new SchedulerThreadPoolImpl(this.name, clock, poolSize);
        new Thread(executor).start();
    }

    public String getName() {
        return name;
    }

    public int getPoolSize(){
        return poolSize;
    }


    /**
     * Add a runnable  to the group set.
     * @param runnable
     * @return
     */
    public synchronized boolean submit(ScheduledRunnable runnable) {
        TaskReferenceImpl taskRef = new OneShotTaskImpl(clock, runnable, new Long(runnable.getExecutionDate()));
        executor.addTask(taskRef);
        jobs.put(runnable, taskRef);
        return true;
    }

    /**
     * Add a runnable  to the group set.
     * @param runnable
     * @return
     */
    public synchronized boolean submit(SpecificClockScheduledRunnable runnable) {
        TaskReferenceImpl taskRef = new OneShotTaskImpl(runnable.getClock(), runnable, new Long(runnable.getExecutionDate()));
        executor.addTask(taskRef);
        jobs.put(runnable, taskRef);
        return true;
    }


    /**
     * Add a runnable  to the group set.
     * @param runnable
     * @return
     */
    public synchronized boolean submit(PeriodicRunnable runnable) {
        TaskReferenceImpl taskRef = new PeriodicTaskImpl(clock, runnable, new Long(runnable.getPeriod()));
        executor.addTask(taskRef);
        jobs.put(runnable, taskRef);
        return true;
    }

    /**
     * Add a runnable  to the group set.
     * @param runnable
     * @return
     */
    public synchronized boolean submit(SpecificClockPeriodicRunnable runnable) {
        TaskReferenceImpl taskRef = new PeriodicTaskImpl(runnable.getClock(), runnable, new Long(runnable.getPeriod()));
        executor.addTask(taskRef);
        jobs.put(runnable, taskRef);
        return true;
    }


    public synchronized boolean withdraw(ICasaRunnable runnable) {
        TaskReferenceImpl handle = jobs.remove(runnable);
        if (handle != null) {
            logger.info("Withdrawing job {}", runnable);
            handle.cancel(true);
            return true;
        }
        return false;
    }

    public synchronized boolean isEmpty() {
        return jobs.isEmpty();
    }

    public synchronized void close() {
        for (TaskReferenceImpl future : jobs.values()) {
            future.cancel(true);
        }
        jobs.clear();
        executor.interrupt();
    }

}
