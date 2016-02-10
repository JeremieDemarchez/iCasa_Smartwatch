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
package fr.liglab.adele.icasa.simulator.device.temperature.impl;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;

/**
 * Implementation of a simulated heater device.
 *
 */
@ContextEntity(services = {Heater.class,SimulatedDevice.class,LocatedObject.class})
public class SimulatedHeaterImpl implements Heater,SimulatedDevice,LocatedObject{

    public final static String SIMULATED_HEATER= "iCasa.Cooler";

    @ContextEntity.State.Field(service = BinaryLight.class,state = BinaryLight.BINARY_LIGHT_POWER_STATUS,directAccess = true,value ="0")
    private double powerLevel;

    @ContextEntity.State.Field(service = SimulatedDevice.class,state = SIMULATED_DEVICE_TYPE,value = SIMULATED_HEATER)
    private String deviceType;

    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_X,directAccess = true,value = "0")
    private int x;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_Y,directAccess = true,value = "10")
    private int y;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.ZONE,directAccess = true,value = LOCATION_UNKNOWN)
    private String zone;

    @Override
    public double getPowerLevel() {
        return powerLevel;
    }

    @Override
    public void setPowerLevel(double level) {
        if (level < 0.0d || level > 1.0d || Double.isNaN(level)){
            throw new IllegalArgumentException("Invalid power level : " + level);
        }
        powerLevel = level;
    }

    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public Position getPosition() {
        return new Position(x,y);
    }

    @Override
    public void setPosition(Position position) {
        x= position.x;
        y=position.y;
    }


}