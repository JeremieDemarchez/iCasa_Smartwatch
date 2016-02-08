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
import fr.liglab.adele.icasa.clockservice.ClockListener;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.concurrent.ManagedScheduledExecutorService;
import org.wisdom.api.concurrent.ManagedScheduledFutureTask;

import java.util.HashMap;
import java.util.Map;

@Component(immediate = true)
@Instantiate
public class SchedulerTechnicalService implements ClockListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerTechnicalService.class);

    @Requires(proxy = false)
    Clock clock;

    @Requires(filter = "(name=" + ManagedScheduledExecutorService.SYSTEM + ")", proxy = false)
    ManagedScheduledExecutorService scheduler;

    private final Map<Long,PeriodicJob> m_periodicJobs = new HashMap<>();

    private final Map<Long,PeriodicRunnable> m_periodicJobsToRun = new HashMap<>();

    @Bind(id = "PeriodicRunnable",aggregate = true,optional = true)
    public synchronized void bindPeriodicRunnable(PeriodicRunnable runnable,ServiceReference serviceReference) {
        Long serviceRefId = (Long) serviceReference.getProperty(Constants.SERVICE_ID);
        LOGGER.info(" Scheduled Runnable " + serviceRefId);
        if (runnable.getPeriod() <= 0){
            LOGGER.error("Periodic Runnable with service Ref" + serviceRefId + " cannot be scheduled because period is equal to 0 or negative");
            return;
        }
        if (runnable.getUnit() == null){
            LOGGER.error("Periodic Runnable with service Ref" + serviceRefId + " cannot be scheduled because unit is null");
            return;
        }
        if (clock != null){
            PeriodicJob job = new PeriodicJob(runnable,clock);
            if(!clock.isPaused()) {
                ManagedScheduledFutureTask task = scheduler.scheduleAtFixedRate(runnable,
                        job.getPeriod(), job.getPeriod(), job.getUnit());
                job.submitted(task);
            }
            m_periodicJobs.put(serviceRefId, job);
        }else {
            m_periodicJobsToRun.put(serviceRefId, runnable);
        }

    }

    @Unbind(id = "PeriodicRunnable")
    public synchronized void unbindPeriodicRunnable(PeriodicRunnable runnable,ServiceReference serviceReference) {
        Long serviceRefId = (Long) serviceReference.getProperty(Constants.SERVICE_ID);
        LOGGER.info(" UnScheduled Runnable " + serviceRefId);
        PeriodicJob job = m_periodicJobs.get(serviceRefId);
        if (job != null) {
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            m_periodicJobs.remove(serviceRefId);
        }
    }

    private final Map<Long,OneShotJob> m_scheduledJobs = new HashMap<>();

    private final Map<Long,ScheduledRunnable> m_scheduledJobsToRun = new HashMap<>();

    @Bind(id = "ScheduledRunnable",aggregate = true,optional = true)
    public synchronized void bindScheduledRunnable(ScheduledRunnable runnable,ServiceReference serviceReference) {
        Long serviceRefId = (Long) serviceReference.getProperty(Constants.SERVICE_ID);
        if (runnable.getExecutionDate() < 0){
            LOGGER.error("Scheduled Runnable with service Ref" + serviceRefId + " cannot be scheduled because execution date is negative");
            return;
        }
        LOGGER.info(" Scheduled Runnable " + serviceRefId);
        if (clock != null){
            OneShotJob job = new OneShotJob(runnable,clock);
            if(!clock.isPaused()) {
                if(job.getExecutionDate() >=0 ) {
                    ManagedScheduledFutureTask task = scheduler.schedule(runnable,
                            job.getExecutionDate(), job.getUnit());
                    job.submitted(task);
                }
            }
            m_scheduledJobs.put(serviceRefId, job);
        } else {
            m_scheduledJobsToRun.put(serviceRefId, runnable);
        }

    }

    @Unbind(id = "ScheduledRunnable")
    public synchronized void unbindScheduledRunnable(ScheduledRunnable runnable,ServiceReference serviceReference) {
        Long serviceRefId = (Long) serviceReference.getProperty(Constants.SERVICE_ID);
        LOGGER.info(" UnScheduled Runnable " + serviceRefId);
        OneShotJob job = m_scheduledJobs.get(serviceRefId);
        if (job != null) {
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            m_scheduledJobs.remove(serviceRefId);
        }
    }

    @Validate
    public synchronized void validate() {
        clock.addListener(this);
        for (Long key : m_periodicJobs.keySet()){
            PeriodicJob job = m_periodicJobs.get(key);
            if (job.task() == null ){
                if(!clock.isPaused()) {
                    ManagedScheduledFutureTask task = scheduler.scheduleAtFixedRate(job.getPeriodicRunnable(),
                            job.getPeriod(), job.getPeriod(), job.getUnit());
                    job.submitted(task);
                }
            }
        }
        for (Long key : m_periodicJobsToRun.keySet()){
            PeriodicJob job = new PeriodicJob(m_periodicJobsToRun.get(key),clock);
            if(!clock.isPaused()) {
                ManagedScheduledFutureTask task = scheduler.scheduleAtFixedRate(m_periodicJobsToRun.get(key),
                        job.getPeriod(), job.getPeriod(), job.getUnit());
                job.submitted(task);
            }
            m_periodicJobs.put(key, job);
        }
        m_periodicJobsToRun.clear();

        for (Long key : m_scheduledJobs.keySet()){
            OneShotJob job = m_scheduledJobs.get(key);
            if (job.task() == null ){
                if(!clock.isPaused()) {
                    if(job.getExecutionDate() >= 0 ) {
                        ManagedScheduledFutureTask task = scheduler.schedule(job.getScheduledRunnable(),
                                job.getExecutionDate(), job.getUnit());
                        job.submitted(task);
                    }
                }
            }
        }

        for (Long key : m_scheduledJobsToRun.keySet()){
            OneShotJob job = new OneShotJob(m_scheduledJobsToRun.get(key),clock);
            if(!clock.isPaused()) {
                if(job.getExecutionDate() >= 0 ) {
                    ManagedScheduledFutureTask task = scheduler.schedule(job.getScheduledRunnable(),
                            job.getExecutionDate(), job.getUnit());
                    job.submitted(task);
                }
            }
            m_scheduledJobs.put(key, job);
        }
        m_scheduledJobsToRun.clear();

    }

    @Invalidate
    public synchronized void invalidate() {
        clock.removeListener(this);
        for (Long key : m_periodicJobs.keySet()) {
            if(m_periodicJobs.get(key).task() != null) {
                m_periodicJobs.get(key).task().cancel(true);
                m_periodicJobs.get(key).submitted(null);
            }
        }
        m_periodicJobs.clear();

        for (Long key : m_scheduledJobs.keySet()) {
            if(m_scheduledJobs.get(key).task() != null) {
                m_scheduledJobs.get(key).task().cancel(true);
                m_scheduledJobs.get(key).submitted(null);
            }
        }
        m_scheduledJobs.clear();
    }

    @Override
    public synchronized void factorModified(int oldFactor) {
        for (Long key : m_periodicJobs.keySet()){
            PeriodicJob job = m_periodicJobs.get(key);
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()){
                ManagedScheduledFutureTask task = scheduler.scheduleAtFixedRate(job.getPeriodicRunnable(),
                        job.getPeriod(), job.getPeriod(), job.getUnit());
                job.submitted(task);
            }
        }
        for (Long key : m_scheduledJobs.keySet()) {
            OneShotJob job = m_scheduledJobs.get(key);
            if(job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()) {
                if(job.getExecutionDate() >= 0 ) {
                    ManagedScheduledFutureTask task = scheduler.schedule(job.getScheduledRunnable(),
                            job.getExecutionDate(), job.getUnit());
                    job.submitted(task);
                }
            }
        }
    }

    @Override
    public synchronized void startDateModified(long oldStartDate) {
        for (Long key : m_scheduledJobs.keySet()) {
            OneShotJob job = m_scheduledJobs.get(key);
            if(job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()) {
                if(job.getExecutionDate() >= 0 ) {
                    ManagedScheduledFutureTask task = scheduler.schedule(job.getScheduledRunnable(),
                            job.getExecutionDate(), job.getUnit());
                    job.submitted(task);
                }
            }
        }
    }

    @Override
    public synchronized void clockPaused() {
        for (Long key : m_periodicJobs.keySet()){
            PeriodicJob job = m_periodicJobs.get(key);
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
        }

        for (Long key : m_scheduledJobs.keySet()) {
            OneShotJob job = m_scheduledJobs.get(key);
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
        }
    }

    @Override
    public synchronized void clockResumed() {
        for (Long key : m_periodicJobs.keySet()){
            PeriodicJob job = m_periodicJobs.get(key);
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()){
                ManagedScheduledFutureTask task = scheduler.scheduleAtFixedRate(job.getPeriodicRunnable(),
                        job.getPeriod(), job.getPeriod(), job.getUnit());
                job.submitted(task);
            }
        }
        for (Long key : m_scheduledJobs.keySet()) {
            OneShotJob job = m_scheduledJobs.get(key);
            if(job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()) {
                if(job.getExecutionDate() >= 0 ) {
                    ManagedScheduledFutureTask task = scheduler.schedule(job.getScheduledRunnable(),
                            job.getExecutionDate(), job.getUnit());
                    job.submitted(task);
                }
            }
        }

    }

    @Override
    public synchronized void clockReset() {
        for (Long key : m_periodicJobs.keySet()){
            PeriodicJob job = m_periodicJobs.get(key);
            if (job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()){
                ManagedScheduledFutureTask task = scheduler.scheduleAtFixedRate(job.getPeriodicRunnable(),
                        job.getPeriod(), job.getPeriod(), job.getUnit());
                job.submitted(task);
            }
        }
        for (Long key : m_scheduledJobs.keySet()) {
            OneShotJob job = m_scheduledJobs.get(key);
            if(job.task() != null) {
                job.task().cancel(true);
                job.submitted(null);
            }
            if(!clock.isPaused()) {
                if(job.getExecutionDate() >= 0 ) {
                    ManagedScheduledFutureTask task = scheduler.schedule(job.getScheduledRunnable(),
                            job.getExecutionDate(), job.getUnit());
                    job.submitted(task);
                }
            }
        }
    }

}
