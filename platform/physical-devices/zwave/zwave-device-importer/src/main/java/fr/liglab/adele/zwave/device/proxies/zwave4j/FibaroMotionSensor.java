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
package fr.liglab.adele.zwave.device.proxies.zwave4j;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.behavior.Behavior;

import fr.liglab.adele.zwave.device.proxies.ZwaveDeviceBehaviorProvider;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.LocatedObjectBehaviorProvider;
import fr.liglab.adele.zwave.device.api.ZwaveController;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;

import java.util.ArrayList;
import java.util.List;

@ContextEntity(services = {MotionSensor.class})
@Behavior(id="LocatedBehavior",spec = LocatedObject.class,implem = LocatedObjectBehaviorProvider.class)
@Behavior(id="ZwaveBehavior",spec = ZwaveDevice.class,implem = ZwaveDeviceBehaviorProvider.class)
public class FibaroMotionSensor implements MotionSensor, GenericDevice  {

    /**
     * iPOJO Require
     */
    @Requires(optional = false, proxy=false)
    private ZwaveController controller;

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;


    /**
     * Services
     */

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }


    
    @Validate
    private void start() {
    }

    @Invalidate
    private void stop() {
    }



}
