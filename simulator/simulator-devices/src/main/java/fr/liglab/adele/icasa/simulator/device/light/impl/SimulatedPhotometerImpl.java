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
package fr.liglab.adele.icasa.simulator.device.light.impl;

import fr.liglab.adele.cream.annotations.behavior.Behavior;
import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.LocatedObjectBehaviorProvider;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.model.api.LuminosityModel;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Modified;
import org.apache.felix.ipojo.annotations.Unbind;

/**
 * Implementation of a simulated photometer device.
 *
 */
@ContextEntity(services = {Photometer.class, SimulatedDevice.class})
@Behavior(id="LocatedBehavior",spec = LocatedObject.class,implem = LocatedObjectBehaviorProvider.class)
public class SimulatedPhotometerImpl implements Photometer, SimulatedDevice,GenericDevice {

    public final static String SIMULATED_PHOTOMETER = "iCasa.Photometer";

    @ContextEntity.State.Field(service = Photometer.class,state=PHOTOMETER_CURRENT_ILLUMINANCE,value = "-1")
    private double currentSensedIlluminance;

    @ContextEntity.State.Field(service = SimulatedDevice.class,state = SIMULATED_DEVICE_TYPE,value = SIMULATED_PHOTOMETER)
    private String deviceType;

    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public double getIlluminance() {
        return currentSensedIlluminance;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Bind(id ="IlluminanceModelDependency" ,filter = "(luminositymodel.zone.attached=${locatedobject.object.zone})",optional = true,aggregate = true)
    public void bindIllu(LuminosityModel model){
        pushIlluminance(model.getCurrentLuminosity());
    }

    @Modified(id = "IlluminanceModelDependency")
    public void modifiedIllu(LuminosityModel model){
        pushIlluminance(model.getCurrentLuminosity());
    }

    @Unbind(id = "IlluminanceModelDependency")
    public void unbindIllu(LuminosityModel model){
        pushIlluminance(FAULT_VALUE);
    }

    @ContextEntity.State.Push(service = Photometer.class,state = Photometer.PHOTOMETER_CURRENT_ILLUMINANCE)
    public double pushIlluminance(double illuminance){
        return illuminance;
    }

}
