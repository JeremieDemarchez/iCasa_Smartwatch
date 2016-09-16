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
///**
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
// */
package fr.liglab.adele.icasa.simulator.device.temperature.impl;

import fr.liglab.adele.cream.annotations.behavior.Behavior;
import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.helpers.location.provider.LocatedObjectBehaviorProvider;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.model.api.TemperatureModel;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Modified;
import org.apache.felix.ipojo.annotations.Unbind;


/**
 * Implementation of a simulated thermometer device.
 *
 */
@ContextEntity(services = {Thermometer.class, SimulatedDevice.class})

@Behavior(id="LocatedBehavior",spec = LocatedObject.class,implem = LocatedObjectBehaviorProvider.class)
public class SimulatedThermometerImpl   implements Thermometer, SimulatedDevice,GenericDevice {

    public final static String SIMULATED_THERMOMETER = "iCasa.Thermometer";

    @ContextEntity.State.Field(service = Thermometer.class,state=Thermometer.THERMOMETER_CURRENT_TEMPERATURE,value = "-1")
    private double currentSensedTemperature;

    @ContextEntity.State.Field(service = SimulatedDevice.class,state = SIMULATED_DEVICE_TYPE,value = SIMULATED_THERMOMETER)
    private String deviceType;

    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public double getTemperature() {
        return currentSensedTemperature;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Bind(id ="TemperatureModelDependency" ,filter = "(temperaturemodel.zone.attached=${locatedobject.object.zone})",optional = true,aggregate = true)
    public void bindTemperature(TemperatureModel model){
        pushTemperature(model.getCurrentTemperature());
    }

    @Modified(id = "TemperatureModelDependency")
    public void modifiedTemperature(TemperatureModel model){
        pushTemperature(model.getCurrentTemperature());
    }

    @Unbind(id = "TemperatureModelDependency")
    public void unbindTemperature(TemperatureModel model){
        pushTemperature(FAULT_VALUE);
    }

    @ContextEntity.State.Push(service = Thermometer.class,state = Thermometer.THERMOMETER_CURRENT_TEMPERATURE)
    public double pushTemperature(double temperature){
        return temperature;
    }

}
