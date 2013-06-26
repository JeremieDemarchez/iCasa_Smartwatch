/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
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
package fr.liglab.adele.icasa.service.scheduler.impl;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable;
import fr.liglab.adele.icasa.service.scheduler.SchedulingManager;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Component(immediate = true)
@Instantiate
@Provides(specifications = {SchedulingManager.class})
public class SchedulingManagerImpl implements SchedulingManager {

    private ConcurrentHashMap<String, Group> groups = new ConcurrentHashMap<String, Group>();
    private Logger logger = LoggerFactory.getLogger(SchedulingManagerImpl.class);

    private BundleContext context;

    public SchedulingManagerImpl(BundleContext ctx){
        context = ctx;
    }

    @Requires
    private Clock clock;

    @Bind(aggregate = true, optional = true)
    public void bindRunnable(ScheduledRunnable runnable) {
        synchronized (this) {
            Group group = groups.get(runnable.getGroup());
            if (group == null) {
                group = new Group(new GroupConfiguration(runnable.getGroup()), clock);
                groups.put(group.getName(), group);
                logger.info("New group created : " + runnable.getGroup());
            }
            group.submit(runnable);
        }
    }

    @Unbind
    public void unbindRunnable(ScheduledRunnable runnable) {
        synchronized (this) {
            Group group = groups.get(runnable.getGroup());
            if (group != null) {
                group.withdraw(runnable);
            }

            // Is the group is empty, close it
            if (group.isEmpty()) {
                group.close();
                groups.remove(group.getName());
            }
        }
    }

    @Bind(aggregate = true, optional = true)
    public void bindPeriodicRunnable(PeriodicRunnable runnable) {
        synchronized (this) {
            Group group = groups.get(runnable.getGroup());
            if (group == null) {
                group = new Group(new GroupConfiguration(runnable.getGroup()), clock);
                groups.put(group.getName(), group);
                logger.info("New group created : " + runnable.getGroup());
            }
            group.submit(runnable);
        }
    }

    @Unbind
    public void unbindPeriodicRunnable(PeriodicRunnable runnable) {
        synchronized (this) {
            Group group = groups.get(runnable.getGroup());
            if (group != null) {
                group.withdraw(runnable);
            }

            // Is the group is empty, close it
            if (group.isEmpty()) {
                group.close();
                groups.remove(group.getName());
            }
        }
    }

    @Validate
    public void start(){
    }
    @Invalidate
    public void stop() {
        for (Group group : groups.values()) {
            group.close();
        }
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
            if (maxThread <= -1){ //if failed, get default configuration.
                maxThread = getMaxThread(PREFIX_PROPERTY + "." + DEFAULT_NAME + "." + MAX_THREAD);
            }
            if (maxThread <= -1) { // If failed, get assign default configuration.
                System.err.println("Unable to assign maxThread to : " + name);
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

