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
package fr.liglab.adele.icasa.device.light.impl;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.State;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;

/**
 * Implementation of a simulated binary light device.
 *
 */
@ContextEntity(services = {BinaryLight.class,SimulatedDevice.class})
public class SimulatedBinaryLightImpl implements BinaryLight, SimulatedDevice{

    public final static String SIMULATED_BINARY_LIGHT = "iCasa.BinaryLight";

    @State.Field(service = BinaryLight.class,state = BinaryLight.BINARY_LIGHT_POWER_STATUS,directAccess = true)
    private boolean powerStatus;

    @State.Field(service = SimulatedDevice.class,state = SIMULATED_DEVICE_TYPE,value = SIMULATED_BINARY_LIGHT)
    private String deviceType;

    @State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_X,directAccess = true)
    private int x;

    @State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_Y,directAccess = true)
    private int y;

    @State.Field(service = LocatedObject.class,state = LocatedObject.ZONE,directAccess = true,value = LOCATION_UNKNOWN)
    private String zone;

    @Override
    public String getDeviceType() {
        return deviceType;
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
        x = position.x;
        y = position.y;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public boolean getPowerStatus() {
        return powerStatus;
    }

    @Override
    public void setPowerStatus(boolean status) {
        powerStatus = status;
    }

    @Override
    public void turnOn() {
        setPowerStatus(true);
    }

    @Override
    public void turnOff() {
        setPowerStatus(false);
    }

}
