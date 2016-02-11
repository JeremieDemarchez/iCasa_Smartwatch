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

// *
// *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
// *   Group Licensed under a specific end user license agreement;
// *   you may not use this file except in compliance with the License.
// *   You may obtain a copy of the License at
// *
// *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
// *
// *   Unless required by applicable law or agreed to in writing, software
// *   distributed under the License is distributed on an "AS IS" BASIS,
// *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *   See the License for the specific language governing permissions and
// *   limitations under the License.
//
///
//// *
//// *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
//// *   Group Licensed under a specific end user license agreement;
//// *   you may not use this file except in compliance with the License.
//// *   You may obtain a copy of the License at
//// *
//// *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
//// *
//// *   Unless required by applicable law or agreed to in writing, software
//// *   distributed under the License is distributed on an "AS IS" BASIS,
//// *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//// *   See the License for the specific language governing permissions and
//// *   limitations under the License.
////
package fr.liglab.adele.icasa.simulator.device.provider;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.simulator.device.light.impl.SimulatedBinaryLightImpl;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.device.SimulatedDeviceProvider;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true,publicFactory=false)
@Provides(specifications = SimulatedDeviceProvider.class)
@Instantiate
public class SimulatedDeviceProviderImpl implements SimulatedDeviceProvider{

    public final static Logger LOG = LoggerFactory.getLogger(SimulatedDeviceProviderImpl.class);

    @Requires(specification = SimulatedDevice.class,optional = true)
    List<SimulatedDevice> simulatedDevices;

    @Creator.Field Creator.Entity<SimulatedBinaryLightImpl> simulatedBinaryLightCreator;

    //   @Entity.Creator.Field Entity.Creator<SimulatedDimmerLightImpl> simulatedDimmerLightCreator;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Override
    public synchronized void createDevice(String deviceType, String deviceId) {
        LOG.info(" Try to create : " + deviceId + " with type " + deviceType);
        if (deviceId == null || deviceType == null){
            return;
        }
        if (checkIfSimulatedDeviceAlreadyExist(deviceId)){
            return;
        }
        Map<String,Object> entityParam = new HashMap<>();
        Creator.Entity creator = this.getCreator(deviceType,entityParam);
        if (creator == null){
            return;
        }

        entityParam.put(ContextEntity.State.ID(GenericDevice.class,GenericDevice.DEVICE_SERIAL_NUMBER),deviceId);
        creator.create(deviceId,entityParam);
    }

    @Override
    public synchronized void removeSimulatedDevice(String deviceId) {
        if (deviceId == null){
            return;
        }
        for (SimulatedDevice device : simulatedDevices){
            if (device.getSerialNumber() != null && device.getSerialNumber().equals(deviceId)){
                Creator.Entity creator = getCreator(device.getDeviceType(),new HashMap<>());
                if (creator == null){
                    return;
                }
                creator.delete(deviceId);
            }
        }
    }

    @Override
    public Set<String> getSimulatedDeviceTypes() {
        Set<String> returnSet = new HashSet<>();
        returnSet.add(SimulatedBinaryLightImpl.SIMULATED_BINARY_LIGHT);
        //     returnSet.add(SimulatedDimmerLightImpl.SIMULATED_DIMMER_LIGHT);
        return returnSet;
    }

    @Override
    public void removeAllSimulatedDevices() {
        simulatedBinaryLightCreator.deleteAll();
        //      simulatedDimmerLightCreator.deleteAllEntities();
    }

    private Creator.Entity getCreator(String deviceType,Map<String,Object> defaultProperties){
        switch (deviceType) {
            case SimulatedBinaryLightImpl.SIMULATED_BINARY_LIGHT:
                defaultProperties.put(ContextEntity.State.ID(BinaryLight.class,BinaryLight.BINARY_LIGHT_POWER_STATUS),false);
                return simulatedBinaryLightCreator;
      /*             case SimulatedDimmerLightImpl.SIMULATED_DIMMER_LIGHT:
             defaultProperties.put(State.ID(DimmerLight.class, DimmerLight.DIMMER_LIGHT_POWER_LEVEL),0d);
             return simulatedDimmerLightCreator;*/
            default:return null;
        }
    }

    private boolean checkIfSimulatedDeviceAlreadyExist(String deviceId){
        if (deviceId == null){
            return true;
        }
        for (SimulatedDevice device : simulatedDevices){
            if (device.getSerialNumber() != null && device.getSerialNumber().equals(deviceId)){
                return true;
            }
        }
        return false;
    }
}
