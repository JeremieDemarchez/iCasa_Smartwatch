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
package fr.liglab.adele.icasa.commands.impl;

import fr.liglab.adele.icasa.commands.AbstractCommand;
import fr.liglab.adele.icasa.commands.ScriptLanguage;
import fr.liglab.adele.icasa.commands.Signature;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.button.PushButton;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.location.Position;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;


/**
 *
 * Sets the fault state of device to "Yes"
 *
 *
 */
@Component(name = "MoveDeviceCommandNew")
@Provides
@Instantiate(name = "move-device-command")
public class MoveDeviceCommand extends AbstractCommand {

    @Requires(specification = BinaryLight.class,optional = true)
    List<BinaryLight> binaryLights;

    @Requires(specification = DimmerLight.class,optional = true)
    List<DimmerLight> dimmerLights;

    @Requires(specification = PresenceSensor.class,optional = true)
    List<PresenceSensor> presenceSensors;

    @Requires(specification = MotionSensor.class,optional = true)
    List<MotionSensor> motionSensors;

    @Requires(specification = PushButton.class,optional = true)
    List<PushButton> pushButtons;

    @Requires(specification = Photometer.class, optional = true)
    List<Photometer> photometers;

    @Requires(specification = Thermometer.class,optional = true)
    List<Thermometer> thermometers;

    @Requires(specification = Cooler.class,optional = true)
    List<Cooler> coolers;

    @Requires(specification = Heater.class,optional = true)
    List<Heater> heaters;

    private static Signature MOVE = new Signature(new String[]{ScriptLanguage.DEVICE_ID, ScriptLanguage.LEFT_X, ScriptLanguage.TOP_Y} );
    private static Signature MOVE_WZ = new Signature(new String[]{ScriptLanguage.DEVICE_ID, ScriptLanguage.LEFT_X, ScriptLanguage.TOP_Y, ScriptLanguage.BOTTOM_Z } );

    public MoveDeviceCommand(){
        addSignature(MOVE);
        addSignature(MOVE_WZ);
    }

    @Override
    public Object execute(InputStream in, PrintStream out,JSONObject param, Signature signature) throws Exception {
        String deviceId = param.getString(ScriptLanguage.DEVICE_ID);

        GenericDevice deviceToMove = findDevice(deviceId);
        if (deviceToMove == null) {
            throw new IllegalArgumentException("Device ("+ deviceId +") does not exist");
        }

        int newX = param.getInt(ScriptLanguage.LEFT_X);
        int newY = param.getInt(ScriptLanguage.TOP_Y);
        deviceToMove.setPosition(new Position(newX,newY));

        return null;
    }

    private GenericDevice findDevice(String deviceSerialNumber){
        for (Heater heater:heaters){
            if (deviceSerialNumber.equals(heater.getSerialNumber()))return heater;
        }
        for (DimmerLight light:dimmerLights){
            if (deviceSerialNumber.equals(light.getSerialNumber()))return light;
        }
        for (Cooler cooler:coolers){
            if (deviceSerialNumber.equals(cooler.getSerialNumber()))return cooler;
        }
        for (Thermometer thermometer:thermometers){
            if (deviceSerialNumber.equals(thermometer.getSerialNumber()))return thermometer;
        }
        for (BinaryLight binaryLight:binaryLights){
            if (deviceSerialNumber.equals(binaryLight.getSerialNumber()))return binaryLight;
        }
        for (PresenceSensor presenceSensor:presenceSensors){
            if (deviceSerialNumber.equals(presenceSensor.getSerialNumber()))return presenceSensor;
        }
        for (MotionSensor motionSensor:motionSensors){
            if (deviceSerialNumber.equals(motionSensor.getSerialNumber()))return motionSensor;
        }
        for (PushButton pushButton:pushButtons){
            if (deviceSerialNumber.equals(pushButton.getSerialNumber()))return pushButton;
        }
        for (Photometer photometer : photometers){
            if (deviceSerialNumber.equals(photometer.getSerialNumber()))return photometer;
        }
        return null;
    }

    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "move-device";
    }

    @Override
    public String getDescription(){
        return "Move a device to new X,Y positions.\n\t" + super.getDescription();
    }

}
