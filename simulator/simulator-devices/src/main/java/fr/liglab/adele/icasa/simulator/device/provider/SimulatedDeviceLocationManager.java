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
package fr.liglab.adele.icasa.simulator.device.provider;

import fr.liglab.adele.icasa.context.model.annotations.provider.Creator;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.device.light.impl.SimulatedBinaryLightImpl;
import fr.liglab.adele.icasa.simulator.device.light.impl.SimulatedDimmerLightImpl;
import fr.liglab.adele.icasa.simulator.device.light.impl.SimulatedPhotometerImpl;
import fr.liglab.adele.icasa.simulator.device.temperature.impl.SimulatedCoolerImpl;
import fr.liglab.adele.icasa.simulator.device.temperature.impl.SimulatedHeaterImpl;
import fr.liglab.adele.icasa.simulator.device.temperature.impl.SimulatedThermometerImpl;
import fr.liglab.adele.icasa.simulator.device.utils.Constant;
import org.apache.felix.ipojo.annotations.*;

import java.util.List;

@Component(immediate = true,publicFactory = false)
@Instantiate
public class SimulatedDeviceLocationManager {

    @Requires(id = "simulatedDevices",specification = SimulatedDevice.class, optional = true)
    List<SimulatedDevice> simulatedDevices;

    @Requires(id = "zones",specification = Zone.class, optional = true)
    List<Zone> zones;

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<SimulatedBinaryLightImpl,Zone> isInBinaryCreator;

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<SimulatedDimmerLightImpl,Zone> isInDimmerCreator;

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<SimulatedPhotometerImpl,Zone> isInPhotometerCreator;

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<SimulatedCoolerImpl,Zone> isInCoolerCreator;

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<SimulatedHeaterImpl,Zone> isInHeaterCreator;

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<SimulatedThermometerImpl,Zone> isInThermometerCreator;

    @Bind(id = "simulatedDevices")
    public void bindSimulatedDevice(SimulatedDevice simulatedDevice){
        Creator.Relation creator = getCreator(simulatedDevice);
        if (creator == null){
            return;
        }
        for (Zone zone:zones){
            if (zone.canContains(simulatedDevice.getPosition())){
                creator.create(simulatedDevice.getSerialNumber(),zone.getZoneName());
            }
        }
    }

    @Modified(id = "simulatedDevices")
    public void modifiedSimulatedDevice(SimulatedDevice simulatedDevice){
        Creator.Relation creator = getCreator(simulatedDevice);
        if (creator == null){
            return;
        }
        for (Zone zone:zones){
            if (zone.canContains(simulatedDevice.getPosition())){
                try {
                    creator.create(simulatedDevice.getSerialNumber(),zone.getZoneName());
                }catch (IllegalArgumentException e){

                }
            }else {
                creator.delete(simulatedDevice.getSerialNumber(),zone.getZoneName());
            }
        }
    }

    @Unbind(id = "simulatedDevices")
    public void unbindSimulatedDevice(SimulatedDevice simulatedDevice){
        Creator.Relation creator = getCreator(simulatedDevice);
        if (creator == null){
            return;
        }
        for (Zone zone:zones){
            if (zone.canContains(simulatedDevice.getPosition())){
                creator.delete(simulatedDevice.getSerialNumber(),zone.getZoneName());
            }
        }
    }

    private Creator.Relation getCreator(SimulatedDevice device){
        if (device instanceof SimulatedBinaryLightImpl){
            return isInBinaryCreator;
        }
        if (device instanceof SimulatedDimmerLightImpl){
            return isInDimmerCreator;
        }
        if (device instanceof SimulatedPhotometerImpl){
            return isInPhotometerCreator;
        }
        if (device instanceof SimulatedCoolerImpl){
            return isInCoolerCreator;
        }
        if (device instanceof SimulatedHeaterImpl){
            return isInHeaterCreator;
        }
        if (device instanceof SimulatedThermometerImpl){
            return isInThermometerCreator;
        }
        return null;
    }
}
