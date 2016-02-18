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
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
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

    @Creator.Field(Constant.RELATION_IS_IN) 	Creator.Relation<LocatedObject,Zone> isInLocatedObjectRelationCreator;

    @Bind(id = "zones")
    public void bindZone(Zone zone){
        for (SimulatedDevice simulatedDevice : simulatedDevices){
            if (zone.canContains(simulatedDevice.getPosition())){
                try {
                    isInLocatedObjectRelationCreator.create(simulatedDevice,zone);
                }catch (IllegalArgumentException e){

                }
            }
        }
    }

    @Modified(id = "zones")
    public void modifiedZone(Zone zone){
        for (SimulatedDevice simulatedDevice : simulatedDevices){
            if (zone.canContains(simulatedDevice.getPosition())){
                try {
                    isInLocatedObjectRelationCreator.create(simulatedDevice,zone);
                }catch (IllegalArgumentException e){

                }
            }else {
                isInLocatedObjectRelationCreator.delete(simulatedDevice,zone);
            }
        }
    }

    @Unbind(id = "zones")
    public void unbindZone(Zone zone){
        for (SimulatedDevice simulatedDevice : simulatedDevices){
            isInLocatedObjectRelationCreator.delete(simulatedDevice,zone);
        }
    }

    @Bind(id = "simulatedDevices")
    public void bindSimulatedDevice(SimulatedDevice simulatedDevice){
        for (Zone zone:zones){
            if (zone.canContains(simulatedDevice.getPosition())){
                try {
                isInLocatedObjectRelationCreator.create(simulatedDevice,zone);
            }catch (IllegalArgumentException e){

            }
            }
        }
    }

    @Modified(id = "simulatedDevices")
    public void modifiedSimulatedDevice(SimulatedDevice simulatedDevice){
        for (Zone zone:zones){
            if (zone.canContains(simulatedDevice.getPosition())){
                try {
                    isInLocatedObjectRelationCreator.create(simulatedDevice,zone);
                }catch (IllegalArgumentException e){

                }
            }else {
                isInLocatedObjectRelationCreator.delete(simulatedDevice,zone);
            }
        }
    }

    @Unbind(id = "simulatedDevices")
    public void unbindSimulatedDevice(SimulatedDevice simulatedDevice){
        isInLocatedObjectRelationCreator.delete(simulatedDevice,simulatedDevice.getZone());
    }

}
