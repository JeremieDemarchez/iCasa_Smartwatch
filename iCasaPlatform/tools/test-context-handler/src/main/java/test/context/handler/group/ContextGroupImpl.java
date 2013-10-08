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
package test.context.handler.group;

import java.util.HashSet;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import fr.liglab.adele.icasa.context.group.ContextGroup;

@Component(name="ContextGroupTestImpl")
@Provides
@Instantiate
public class ContextGroupImpl implements ContextGroup {

    private Set<String> zones;
    
    public ContextGroupImpl() {
       zones = new HashSet<String>();
        zones.add("zone-1");
        zones.add("zone-2");
    }
    
    @Override
    public String getGroupId() {
        return "test-group-1";
    }

    @Override
    public Set<String> getPrivateZones() {

        return zones;
    }

    @Override
    public Set<String> getPrivateVariables() {
        Set<String> vars = new HashSet<String>();
        return vars;
    }



    
}
