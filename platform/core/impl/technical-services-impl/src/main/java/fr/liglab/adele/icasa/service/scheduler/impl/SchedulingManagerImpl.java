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

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.*;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Component(immediate = true)
@Instantiate
@Provides(specifications = {SchedulingManager.class})
public class SchedulingManagerImpl implements SchedulingManager {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG+".scheduler");

    private ConcurrentHashMap<String, Group> sharedClockedGroups = new ConcurrentHashMap<String, Group>();
    private ConcurrentHashMap<String, Group> specificClockedGroups = new ConcurrentHashMap<String, Group>();

    //Stock reference for rescheduling when clock appear/disappear.
    private Set<ScheduledRunnable> scheduledRunables = new HashSet<ScheduledRunnable>();
    private Set<PeriodicRunnable> periodicRunnables = new HashSet<PeriodicRunnable>();

    //Stock reference for rescheduling when clock appear/disappear.
    //private Set<SpecificClockScheduledRunnable> specificClockScheduledRunnables = new HashSet<SpecificClockScheduledRunnable>();
    //private Set<SpecificClockPeriodicRunnable> specificClockPeriodicRunnables = new HashSet<SpecificClockPeriodicRunnable>();

    private BundleContext context;

    public SchedulingManagerImpl(BundleContext ctx){
        context = ctx;
    }

    private Clock clock;

    @Bind(aggregate = true, optional = true)
    public void bindRunnable(ScheduledRunnable runnable) {
        synchronized (this) {
            if(clock != null){
                submitScheduledRunnable(runnable);
            }
            scheduledRunables.add(runnable);
        }
    }

    private synchronized void submitScheduledRunnable(ScheduledRunnable runnable){
        Group group = null;
        GroupConfiguration configuration = new GroupConfiguration(runnable.getGroup());

        group = sharedClockedGroups.get(configuration.getName());

        if (group == null) {
            group = new Group(configuration, clock);
            sharedClockedGroups.put(group.getName(), group);
            logger.info("New group created : " + group.getName());
        }
        group.submit(runnable);
    }

    @Unbind
    public void unbindRunnable(ScheduledRunnable runnable) {
        synchronized (this) {
            String groupName = runnable.getGroup();
            if (groupName == null){
                groupName = GroupConfiguration.DEFAULT_NAME;
            }

            Group group = sharedClockedGroups.get(groupName);
            if (group != null) {
                group.withdraw(runnable);
                
                // Is the group is empty, close it
                if (group.isEmpty()) {
                    group.close();
                    sharedClockedGroups.remove(group.getName());
                }
            }

            scheduledRunables.remove(runnable);
        }
    }

    @Bind(aggregate = true, optional = true)
    public void bindSpecificClockRunnable(SpecificClockScheduledRunnable runnable) {
        synchronized (this) {
            Group group = null;
            GroupConfiguration configuration = new GroupConfiguration(runnable.getGroup());
            group = specificClockedGroups.get(configuration.getName());

            if (group == null) {
                group = new Group(configuration, runnable.getClock());
                specificClockedGroups.put(group.getName(), group);
                logger.info("New group created : " + group.getName());
            }
            group.submit(runnable);
        }
    }

    @Unbind
    public void unbindSpecificClockRunnable(SpecificClockScheduledRunnable runnable) {
        synchronized (this) {
            String groupName = runnable.getGroup();
            if (groupName == null){
                groupName = GroupConfiguration.DEFAULT_NAME;
            }

            Group group = specificClockedGroups.get(groupName);
            if (group != null) {
                group.withdraw(runnable);
                
                // Is the group is empty, close it
                if (group.isEmpty()) {
                    group.close();
                    specificClockedGroups.remove(group.getName());
                }
            }
        }
    }

    @Bind(aggregate = true, optional = true)
    public void bindPeriodicRunnable(PeriodicRunnable runnable) {
        synchronized (this) {
            if(clock != null){
                submitPeriodicRunnable(runnable);
            }
            periodicRunnables.add(runnable);
        }
    }

    private synchronized void submitPeriodicRunnable(PeriodicRunnable runnable){
        Group group = null;
        GroupConfiguration configuration = new GroupConfiguration(runnable.getGroup());
        group = sharedClockedGroups.get(configuration.getName());

        if (group == null) {
            group = new Group(configuration, clock);
            sharedClockedGroups.put(group.getName(), group);
            logger.info("New group created : " + group.getName());
        }
        group.submit(runnable);
    }

    @Unbind
    public void unbindPeriodicRunnable(PeriodicRunnable runnable) {
        synchronized (this) {
            String groupName = runnable.getGroup();
            if (groupName == null){
                groupName = GroupConfiguration.DEFAULT_NAME;
            }
            Group group = sharedClockedGroups.get(groupName);
            if (group != null) {
                group.withdraw(runnable);
                
                // If the group is empty, close it
                if (group.isEmpty()) {
                    group.close();
                    sharedClockedGroups.remove(group.getName());
                }
            }
            periodicRunnables.remove(runnable);
        }
    }

    @Bind(aggregate = true, optional = true)
    public void bindSpecificClockPeriodicRunnable(SpecificClockPeriodicRunnable runnable) {
        synchronized (this) {
            Group group = null;
            GroupConfiguration configuration = new GroupConfiguration(runnable.getGroup());
            group = specificClockedGroups.get(configuration.getName());

            if (group == null) {
                group = new Group(configuration, runnable.getClock());
                specificClockedGroups.put(group.getName(), group);
                logger.info("New group created : " + group.getName());
            }
            group.submit(runnable);
        }
    }

    @Unbind
    public void unbindSpecificClockPeriodicRunnable(SpecificClockPeriodicRunnable runnable) {
        synchronized (this) {
            String groupName = runnable.getGroup();
            if (groupName == null){
                groupName = GroupConfiguration.DEFAULT_NAME;
            }
            Group group = specificClockedGroups.get(groupName);
            if (group != null) {
                group.withdraw(runnable);
                
                // If the group is empty, close it
                if (group.isEmpty()) {
                    group.close();
                    specificClockedGroups.remove(group.getName());
                }
            }
        }
    }

    @Bind(aggregate = false, optional = true)
    public void bindClock(Clock clock){
        logger.trace("Appear clock");
        this.clock = clock;
        for(ScheduledRunnable runnable: scheduledRunables){
            submitScheduledRunnable(runnable);
        }
        for(PeriodicRunnable runnable: periodicRunnables){
            submitPeriodicRunnable(runnable);
        }
    }
    @Unbind
    public void unbindClock(Clock clock){
        logger.trace("Disappear clock");
        this.clock = null;
        //we stop only shared clocked group.
        for (Group group : sharedClockedGroups.values()) {
            group.close();
        }
        sharedClockedGroups.clear();
    }


    @Validate
    public synchronized void validate(){
        logger.trace("validate");
    }

    @Invalidate
    public void invalidate() {
        logger.trace("Invalidate");
        for (Group group : sharedClockedGroups.values()) {
            group.close();
        }
        sharedClockedGroups.clear();

        for (Group group : specificClockedGroups.values()) {
            group.close();
        }
        specificClockedGroups.clear();
    }

    public class GroupConfiguration {

        private final static String DEFAULT_NAME="default";
        private final static String NAME="name";
        private final static String MAX_THREAD="maxThread";
        private final static String PREFIX_PROPERTY="iCasa.ThreadPool";
        private final static int DEFAULT_MAX_THREAD=5;


        private int maxThread;
        private final String name;

        GroupConfiguration (String groupName) {
            if (groupName == null){
                name = DEFAULT_NAME;
            } else{
                name = groupName;
            }
            //get group configuration.
            maxThread = getMaxThread(PREFIX_PROPERTY + "." + name + "." + MAX_THREAD);
            if (maxThread <= -1){ //if failed, get default configuration from properties.
                maxThread = getMaxThread(PREFIX_PROPERTY + "." + DEFAULT_NAME + "." + MAX_THREAD);
            }
            if (maxThread <= -1) { // If failed, assign default configuration.
                maxThread = DEFAULT_MAX_THREAD;
            }
        }

        int getMaxThread(String property){
            String maxThreadStr = context.getProperty(property);
            int max = -1;
            if (maxThreadStr != null){
                try{
                    max = Integer.parseInt(maxThreadStr);
                }catch (Exception ex){
                    max = -1;
                }
            }
            return max;
        }


        public int getMaxThread() {
            return maxThread;
        }

        public String getName() {
            return name;
        }

    }

}

