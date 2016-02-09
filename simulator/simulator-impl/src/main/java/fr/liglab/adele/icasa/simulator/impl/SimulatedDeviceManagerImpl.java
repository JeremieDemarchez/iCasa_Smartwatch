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
package fr.liglab.adele.icasa.simulator.impl;

import fr.liglab.adele.icasa.context.model.annotations.entity.State;
import fr.liglab.adele.icasa.context.model.annotations.provider.Entity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.light.impl.SimulatedBinaryLightImpl;
import fr.liglab.adele.icasa.device.light.impl.SimulatedDimmerLightImpl;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDeviceManager;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Provides(specifications = SimulatedDeviceManager.class)
@Instantiate
public class SimulatedDeviceManagerImpl implements SimulatedDeviceManager{

    public final static Logger LOG = LoggerFactory.getLogger(SimulatedDeviceManagerImpl.class);

    @Requires(specification = SimulatedDevice.class,optional = true)
    List<SimulatedDevice> simulatedDevices;

    @Entity.Creator.Field Entity.Creator<SimulatedBinaryLightImpl> simulatedBinaryLightCreator;

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
        Entity.Creator creator = this.getCreator(deviceType,entityParam);
        if (creator == null){
            return;
        }

        entityParam.put(State.ID(GenericDevice.class,GenericDevice.DEVICE_SERIAL_NUMBER),deviceId);
        creator.createEntity(deviceId,entityParam);
    }

    @Override
    public synchronized void removeSimulatedDevice(String deviceId) {
        if (deviceId == null){
            return;
        }
        for (SimulatedDevice device : simulatedDevices){
            if (device.getSerialNumber() != null && device.getSerialNumber().equals(deviceId)){
                Entity.Creator creator = getCreator(device.getDeviceType(),new HashMap<>());
                if (creator == null){
                    return;
                }
                creator.deleteEntity(deviceId);
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
        simulatedBinaryLightCreator.deleteAllEntities();
  //      simulatedDimmerLightCreator.deleteAllEntities();
    }

    private Entity.Creator getCreator(String deviceType,Map<String,Object> defaultProperties){
        defaultProperties.put(State.ID(LocatedObject.class,LocatedObject.OBJECT_X),40);
        defaultProperties.put(State.ID(LocatedObject.class,LocatedObject.OBJECT_Y),40);
        switch (deviceType) {
            case SimulatedBinaryLightImpl.SIMULATED_BINARY_LIGHT:
                defaultProperties.put(State.ID(BinaryLight.class,BinaryLight.BINARY_LIGHT_POWER_STATUS),false);
                return simulatedBinaryLightCreator;
  /**          case SimulatedDimmerLightImpl.SIMULATED_DIMMER_LIGHT:
                defaultProperties.put(State.ID(DimmerLight.class, DimmerLight.DIMMER_LIGHT_POWER_LEVEL),0d);
                return simulatedDimmerLightCreator;**/
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