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
package fr.liglab.adele.icasa.context.extensions.remote.configuration;


import fr.liglab.adele.icasa.context.extensions.remote.api.ControllerConfigurator;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import java.util.HashSet;
import java.util.Set;

@Component(immediate = true)
@Instantiate
@Provides(specifications = ControllerConfigurator.class)
public class ControllerConfiguratorSimulatorImpl implements ControllerConfigurator {

    private static final String GROUP_PLATFORM = "platform";
    private static final String GROUP_SIMULATOR = "simulator";
    private static final String GROUP_APP = "application";

    private static final String base = "fr.liglab.adele.icasa.";
    private static final String platform_location = "location";
    private static final String platform_device = "device";
    private static final String platform_clock = "clockservice";
    private static final String simulator = "simulator";
    private static final String application = "application";

    public String getEntityGroup(ContextEntity contextEntity){
        if(contextEntity == null){
            return GROUP_DEFAULT;
        }

        Set<String> groupSet = new HashSet<>();
        String group;
        int offset;

        Set<String> services = contextEntity.getServices();
        for(String service : services){
            if(service.startsWith(base)){
                offset = base.length();

                if(service.startsWith(application, offset)){
                    groupSet.add(GROUP_APP);
                } else if(service.startsWith(simulator, offset)){
                    groupSet.add(GROUP_SIMULATOR);
                } else if(service.startsWith(platform_location, offset)
                        || service.startsWith(platform_device, offset)
                        || service.startsWith(platform_clock, offset)){
                    groupSet.add(GROUP_PLATFORM);
                }
            }
        }

        /**
         * Define group by priority
         */
        if (groupSet.contains(GROUP_APP)){
            group = GROUP_APP;
        } else if(groupSet.contains(GROUP_SIMULATOR)){
            group = GROUP_SIMULATOR;
        } else if (groupSet.contains(GROUP_PLATFORM)){
            group = GROUP_PLATFORM;
        } else {
            group = GROUP_DEFAULT;
        }


        //TODO: define priority for services group
        return group;
    }
}
