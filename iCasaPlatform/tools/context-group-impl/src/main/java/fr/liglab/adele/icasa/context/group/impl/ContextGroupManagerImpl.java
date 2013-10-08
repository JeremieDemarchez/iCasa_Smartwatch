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
package fr.liglab.adele.icasa.context.group.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.context.group.ContextGroup;
import fr.liglab.adele.icasa.context.group.ContextGroupManager;

@Component(name="ContextGroupManagerImpl")
@Provides
@Instantiate
public class ContextGroupManagerImpl implements ContextGroupManager {

    private Map<String, ContextGroup> groups;
    
    public ContextGroupManagerImpl() {
        groups = new HashMap<String, ContextGroup>();
    }
    
    @Override
    public ContextGroup getContextGroup(String groupId) {
        ContextGroup contextGroup = null;
        synchronized (groups) {
            contextGroup = groups.get(groupId);
        }
        return contextGroup;
    }
    
    @Bind(optional=true) 
    public void bindContextGroup(ContextGroup contextGroup) {
        synchronized (groups) {
            groups.put(contextGroup.getGroupId(), contextGroup);
        }
    }
    
    @Unbind
    public void unbindContextGroup(ContextGroup contextGroup) {
        synchronized (groups) {
            groups.remove(contextGroup.getGroupId());
        }
    }
    
    @Validate
    public void start() {

    }

    @Invalidate
    public void stop() {
        synchronized (groups) {
            groups.clear();
        }
    }

    @Override
    public String isPrivateZone(String zoneId) {
        List<ContextGroup> tempGroups = new ArrayList<ContextGroup>();
        synchronized (groups) {
            tempGroups.addAll(groups.values());
        }
        for (ContextGroup contextGroup : tempGroups) {
            Set<String> privateZones = contextGroup.getPrivateZones();
            if (privateZones.contains(zoneId)) {
                return contextGroup.getGroupId();
            }
        } 
        return null;
    }
}
