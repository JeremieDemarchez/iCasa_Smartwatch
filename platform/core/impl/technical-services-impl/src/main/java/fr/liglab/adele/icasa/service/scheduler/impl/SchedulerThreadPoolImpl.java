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

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.clock.ClockListener;
import fr.liglab.adele.icasa.service.scheduler.TaskReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;


public class SchedulerThreadPoolImpl extends Thread implements ClockListener {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);

    // The access lock
    private final Object m_lock;

    // The parent service
    private final Clock clockService;

    private static final int DEFAULT_PERIOD = 100;

    private static final int MINIMAL_PERIOD = 5;

    private volatile boolean running = true;

    // The threads in charge of task executions
    private final ExecutorThreadImpl[] m_executors;

    // The tasks to execute, indexed by next wanted iteration time
    private final TreeMap/* <Long, List<TaskReferenceImpl>> */m_tasks;

    // TODO
    public SchedulerThreadPoolImpl(String groupName, Clock service, int poolSize) {
        super(groupName);
        m_lock = new Object();
        clockService = service;
        m_executors = new ExecutorThreadImpl[poolSize];
        m_tasks = new TreeMap/* <Long, List<TaskReferenceImpl>> */();
        clockService.addListener(this);
    }

    // TODO
    public void addTask(TaskReferenceImpl task) {
        long baseTime = task.getRegistrationTime();
        Long expectedTime = task.computeNextExecutionTime(baseTime);
        addTask(task,expectedTime);
    }

    private void addTask(TaskReferenceImpl task, Long expectedTime){
        if (expectedTime == null) {
            logger.warn("Ignoring task : Cannot determine first execution time");
            return;
        }
        synchronized (m_lock) {
            List/* <TaskReferenceImpl> */list = (List) m_tasks
                    .get(expectedTime);
            if (list == null) {
                list = new ArrayList(1);
                list.add(task);
                m_tasks.put(expectedTime, list);
            } else {
                list.add(task);
            }
        }
    }

    public void close() {
        this.running = false;
    }

    @Override
    public void factorModified(int oldFactor) {
        //do nothing
    }

    @Override
    public void startDateModified(long oldStartDate) {
        rescheduleTask();
    }

    @Override
    public void clockPaused() {
        //do nothning
    }

    @Override
    public void clockResumed() {
        //Do nothing
    }

    @Override
    public void clockReset() {
        rescheduleTask();
    }

    private void rescheduleTask(){
        // TODO : CHECK WHEN RECSHEDULING IF TASK ARE EXECUTED
        synchronized (m_lock) {
            Long now = new Long(clockService.currentTimeMillis());
            TreeMap temp;
            temp = new TreeMap(m_tasks);
            m_tasks.clear();

            for(Object tempObj : temp.keySet()){
               List list = (List) temp
                        .get(tempObj);
                if (list != null) {

                    TaskReferenceImpl task= (TaskReferenceImpl) list.get(0);
                    long expectedTime = task.computeNextExecutionTime(now);
                    m_tasks.put(expectedTime, list);
                }
            }
        }
    }
    // TODO
    public void run() {
        boolean isRunning = this.running;
       while (isRunning) {
            Long now = new Long(clockService.currentTimeMillis());
            synchronized (m_lock) {
                SortedMap toRun = m_tasks.headMap(now, true);
                List<TaskReferenceImpl> toReschedule = new ArrayList<TaskReferenceImpl>();
                Iterator i = toRun.entrySet().iterator();
                while (i.hasNext()) {
                    Entry e = (Entry) i.next();
                    List list = (List) e.getValue();
                    Iterator j = list.iterator();
                    while (j.hasNext()) {
                        TaskReferenceImpl task = (TaskReferenceImpl) j.next();
                        if (scheduleNow(task)) {
                            j.remove();
                            Long ntime = task.computeNextExecutionTime(now);
                            if (ntime != null){
                                toReschedule.add(task);
                            }
                        }
                    }
                    // Remove from queue if list is empty
                    if (list.isEmpty()) {
                        i.remove();
                    }
                }
                //reschedule periodic tasks.
                for (TaskReferenceImpl task: toReschedule){
                    addTask(task, task.getNextExecutionTime());
                }
            }
            try {
                int period = DEFAULT_PERIOD;
                if (clockService.getFactor() > 100){
                    period = DEFAULT_PERIOD*100 / clockService.getFactor();
                    if (period < MINIMAL_PERIOD){
                        period = MINIMAL_PERIOD; //minimal factor.
                    }
                }

                Thread.sleep(period);
            } catch (InterruptedException e) {
                this.running = false;
                isRunning = this.running;
            }
            isRunning = this.running;
        }
        // Interrupt all executors
        synchronized (m_lock) {
            for (int i = 0; i < m_executors.length; i++) {
                ExecutorThreadImpl executor = m_executors[i];
                if (executor != null) {
                    executor.interrupt();
                }
            }
            clockService.removeListener(this);
        }
    }

    private boolean scheduleNow(TaskReferenceImpl task) {
        for (int i = 0; i < m_executors.length; i++) {
            ExecutorThreadImpl executor = m_executors[i];
            if (executor == null || !executor.isAlive()) {
                executor = new ExecutorThreadImpl(i, task);
                m_executors[i] = executor;
                executor.start();
                return true;
            } else if (executor.getCurrentTask() == null) {
                executor.setCurrentTask(task);
                return true;
            }
        }
        return false;
    }

    private static class ExecutorThreadImpl extends Thread {

        private final Object m_lock;
        private volatile TaskReferenceImpl m_currentTask;

        public ExecutorThreadImpl(int slotNumber, TaskReferenceImpl task) {
            super("ExecutorThread-" + slotNumber);
            m_lock = new Object();
            m_currentTask = task;
        }

        public TaskReferenceImpl getCurrentTask() {
            synchronized (m_lock) {
                return m_currentTask;
            }
        }

        public void setCurrentTask(TaskReferenceImpl task) {
            synchronized (m_lock) {
                if (m_currentTask != null) {
                    throw new IllegalStateException();
                }
                m_currentTask = task;
                m_lock.notify();
            }
        }

        // TODO
        public void run() {
            for (;;) {
                synchronized (m_lock) {
                    if (m_currentTask != null) {
                        try{
                            m_currentTask.run();
                        }catch(Throwable ex){
                        }
                        m_currentTask = null;
                    }
                    try {
                        m_lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }
    }

}
