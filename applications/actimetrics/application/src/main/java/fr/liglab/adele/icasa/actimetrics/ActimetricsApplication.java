/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.actimetrics;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.button.PushButton;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;

@Component(name = "ActimetricsApplication")
@Instantiate
public class ActimetricsApplication  {


    protected static final String APPLICATION_ID = "actimetrics";

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG + APPLICATION_ID);

    @RequiresDevice(id = "motionSensors", type = "field", optional = true)
    private MotionSensor[] motionSensors;

    @RequiresDevice(id = "pushButtons", type = "field", optional = true)
    private PushButton[] pushButtons;
    
    @RequiresDevice(id = "presenceSensors", type = "field", optional = true)
    private PresenceSensor[] presenceSensors; 
    
    @RequiresDevice(id = "powerSwitchs", type = "field", optional = true)
    private PowerSwitch[] powerSwitchs;


    /** Bind Method for null dependency */
    // @RequiresDevice(id = "pushButtons", type = "bind")
    public void bindPushButtons(PushButton button) {
        logger.trace("Register Listener to PushButton: " + button.getSerialNumber());
    }

    /** Unbind Method for null dependency */
    // @RequiresDevice(id = "pushButtons", type = "unbind")
    public void unbindPushButtons(PushButton button) {
        logger.trace("Remove Listener to PushButton: " + button.getSerialNumber());
    }

    /** Bind Method for null dependency */
    @RequiresDevice(id = "motionSensors", type = "bind")
    public void bindMotionSensor(MotionSensor motionSensor) {
        logger.trace("Register Listener to MotionSensor" + motionSensor.getSerialNumber());
    }

    /** Unbind Method for null dependency */
    @RequiresDevice(id = "motionSensors", type = "unbind")
    public void unbindMotionSensor(MotionSensor motionSensor) {
        logger.trace("Remove Listener to MotionSensor" + motionSensor.getSerialNumber());
    }


    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        // do nothing
    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        // do nothing
    }




}
