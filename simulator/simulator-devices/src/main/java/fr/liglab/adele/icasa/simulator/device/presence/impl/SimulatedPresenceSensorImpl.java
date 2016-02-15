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
package fr.liglab.adele.icasa.simulator.device.presence.impl;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity.State;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity.Relation;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.device.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.device.utils.Constant;
import fr.liglab.adele.icasa.simulator.model.api.PresenceModel;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Modified;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

/**
 * Implementation of a simulated binary light device.
 *
 */
@ContextEntity(services = {LocatedObject.class,PresenceSensor.class,SimulatedDevice.class})
public class SimulatedPresenceSensorImpl implements LocatedObject,PresenceSensor,SimulatedDevice{

    public final static String SIMULATED_PRESENCE_SENSOR = "iCasa.PresenceSensor";

    @State.Field(service = PresenceSensor.class,state = PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE,value = "false")
    private boolean currentSensedPresence;

    @State.Field(service = SimulatedDevice.class,state = SIMULATED_DEVICE_TYPE,value = SIMULATED_PRESENCE_SENSOR)
    private String deviceType;

    @State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_X,directAccess = true,value = "0")
    private int x;

    @State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_Y,directAccess = true,value = "0")
    private int y;

    @State.Field(service = LocatedObject.class,state = LocatedObject.ZONE,value = LOCATION_UNKNOWN)
    private String zoneName;


    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public String getZone() {
        return zoneName;
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
    public boolean getSensedPresence() {
        return currentSensedPresence;
    }

    /**
     * Zone
     */

    @Relation.Field(Constant.RELATION_IS_IN)
    @Requires(id="zone",specification=Zone.class,optional=true)
    private Zone zone;

    @Bind(id = "zone")
    public void bindZone(Zone zone){
        pushZone(zone.getZoneName());
    }

    @Unbind(id= "zone")
    public void unbindZone(Zone zone){
        pushZone(LOCATION_UNKNOWN);
    }

    @State.Push(service = LocatedObject.class,state = LocatedObject.ZONE)
    public String pushZone(String zoneName) {
        return zoneName;
    }

    /**
     * Presence
     */

    @Requires(id="presence.model",specification=PresenceModel.class,optional=true,filter = "(presencemodel.zone.attached=${locatedobject.object.zone})")
    private PresenceModel presenceModel;

    @Bind(id ="presence.model")
    public void bindPresenceModel(PresenceModel model){
        pushPresence(model.getCurrentPresence());
    }

    @Modified(id = "presence.model")
    public void modifiedPresenceModel(PresenceModel model){
        pushPresence(model.getCurrentPresence());
    }

    @Unbind(id = "presence.model")
    public void unbindPresenceModel(PresenceModel model){
        pushPresence(false);
    }

    @State.Push(service = PresenceSensor.class,state = PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)
    public boolean pushPresence(boolean presence){
        return presence;
    }
}
