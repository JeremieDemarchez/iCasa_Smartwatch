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

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.model.api.LuminosityModel;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Modified;
import org.apache.felix.ipojo.annotations.Requires;

/**
 * Implementation of a simulated photometer device.
 *
 */
@ContextEntity(services = {Photometer.class, SimulatedDevice.class, LocatedObject.class})
public class SimulatedPhotometerImpl implements Photometer, SimulatedDevice,LocatedObject {

    public final static String SIMULATED_PHOTOMETER = "iCasa.Photometer";

    @ContextEntity.State.Field(service = Photometer.class,state=PHOTOMETER_CURRENT_ILLUMINANCE)
    private double currentSensedIlluminance;

    @ContextEntity.State.Field(service = SimulatedDevice.class,state = SIMULATED_DEVICE_TYPE,value = SIMULATED_PHOTOMETER)
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
    public double getIlluminance() {
        return currentSensedIlluminance;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Requires(id = "luminosity")
    LuminosityModel luminosityModel;

    @Bind(id = "luminosity")
    public void bindLuminosity(LuminosityModel model){
        pushIlluminance(model.getCurrentLuminosity());
    }

    @Modified(id = "luminosity")
    public void modifiedLuminosity(LuminosityModel model){
        pushIlluminance(model.getCurrentLuminosity());
    }

    @ContextEntity.State.Push(service = Photometer.class,state = Photometer.PHOTOMETER_CURRENT_ILLUMINANCE)
    public double pushIlluminance(double illuminance){
        return illuminance;
    }
}
