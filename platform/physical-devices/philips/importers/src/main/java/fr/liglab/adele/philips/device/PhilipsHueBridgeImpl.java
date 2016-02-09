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
package fr.liglab.adele.philips.device;

import com.philips.lighting.hue.sdk.bridge.impl.PHBridgeImpl;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import fr.liglab.adele.philips.device.util.Constants;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.fuchsia.core.component.AbstractDiscoveryComponent;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryIntrospection;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclarationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true,name = "philipsHueBridge")
@Provides(specifications = {DiscoveryIntrospection.class,DiscoveryIntrospection.class,PhilipsHueBridge.class})
public class PhilipsHueBridgeImpl extends AbstractDiscoveryComponent implements PhilipsHueBridge{

    Timer timer;

    private Set<String> lamps = new HashSet<>();

    private Map<String, FetchBridgeLampsTask> lampsSearchTask = new HashMap<String, FetchBridgeLampsTask>();

    @ServiceProperty(name = "bridge.id",mandatory = true)
    private String bridgeId;

    @ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
    private String name;

    @ServiceProperty(name="philips.bridge",mandatory = true)
    private PHBridge bridge;

    protected PhilipsHueBridgeImpl(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    public void start(){
        super.start();
        timer = new Timer();
        FetchBridgeLampsTask task=new FetchBridgeLampsTask((PHBridgeImpl) bridge,lamps);

        timer.schedule(task, 0, 5000);

    }

    @Override
    public void stop(){
        super.stop();

        timer.cancel();

        timer.purge();

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void updateLightState(PHLight var1, PHLightState var2) {
        bridge.updateLightState(var1,var2);
    }


    public class FetchBridgeLampsTask extends TimerTask {

        private final Logger LOG = LoggerFactory.getLogger(FetchBridgeLampsTask.class);

        private final PHBridge bridge;

        private final Set<String> lamps;

        public FetchBridgeLampsTask(PHBridge bridge,final Set<String> lamps){
            this.bridge=bridge;
            this.lamps=lamps;
        }

        @Override
        public void run() {

            for(PHLight light:bridge.getResourceCache().getAllLights()){

                if(!light.isReachable()){
                    ImportDeclaration importDeclarationToRemove = null;
                    for (ImportDeclaration declaration : getImportDeclarations()){
                        for (Map.Entry<String,Object> metadatas : declaration.getMetadata().entrySet()){
                            if (metadatas.getKey().equals(org.ow2.chameleon.fuchsia.core.declaration.Constants.ID) && metadatas.getValue().equals(light.getIdentifier())){
                                importDeclarationToRemove = declaration;
                                break;
                            }
                        }
                    }

                    if (importDeclarationToRemove!= null){
                        unregisterImportDeclaration(importDeclarationToRemove);
                    }
                }else if(!lamps.contains(light.getIdentifier())){

                    Map<String, Object> metadata = new HashMap<String, Object>();

                    metadata.put(org.ow2.chameleon.fuchsia.core.declaration.Constants.ID, light.getIdentifier());
                    metadata.put(Constants.DISCOVERY_PHILIPS_DEVICE_NAME, light.getModelNumber());
                    metadata.put(Constants.DISCOVERY_PHILIPS_DEVICE_TYPE, light.getClass().getName());
                    metadata.put(Constants.DISCOVERY_PHILIPS_DEVICE_OBJECT, light);
                    metadata.put(Constants.DISCOVERY_PHILIPS_DEVICE_BRIDGE, bridge);

                    ImportDeclaration declaration = ImportDeclarationBuilder.fromMetadata(metadata).build();

                    registerImportDeclaration(declaration);

                    if(lamps.contains(light.getIdentifier())){
                        LOG.warn("Lamp with identifier {} already exists",light.getIdentifier());
                    }

                    lamps.add(light.getIdentifier());

                }
            }
        }
    }
}
