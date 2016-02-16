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

import java.util.Set;

@Component(immediate = true)
@Instantiate
@Provides(specifications = ControllerConfigurator.class)
public class ControllerConfiguratorSimulatorImpl implements ControllerConfigurator {
    private static String simulator_package = "fr.liglab.adele.icasa.simulator";


    public int getEntityGroup(ContextEntity contextEntity) {
        if(contextEntity == null){
            return 0;
        }

        Set<String> services = contextEntity.getServices();
        for(String service : services){
            if(service.contains(simulator_package)){
                return 1;
            }
        }
        return 0;
    }
}
