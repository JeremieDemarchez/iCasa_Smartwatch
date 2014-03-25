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
package fr.liglab.adele.philips.device;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLightState;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.ColorLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Philips Hue light implementation that allows to change its state (color, brightness)
 * @author Jander Nascimento
 */
@Component(name = "philipsHueLight")
@Provides(specifications={GenericDevice.class, BinaryLight.class, DimmerLight.class, ColorLight.class})
public class PhilipsHueLight extends AbstractDevice implements
        ColorLight {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String serialNumber;

    @ServiceProperty(name="philips.device.light",mandatory = true)
    private PHLight light;

    @ServiceProperty(name="philips.device.bridge",mandatory = true)
    private PHBridge bridge;

    private static final Logger LOG = LoggerFactory.getLogger(PhilipsHueLight.class);

    public PhilipsHueLight(){
        super();
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, GenericDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS, false);
        super.setPropertyValue(BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL, 8.5d);
        super.setPropertyValue(DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL, 8.5d);
        super.setPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL, 0.0d);
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public boolean getPowerStatus() {
        return light.getLastKnownLightState().isOn();
    }

    @Override
    public boolean setPowerStatus(boolean value) {
        PHLightState lightState = new PHLightState();
        lightState.setOn(value);
        bridge.updateLightState(light, lightState);
        super.setPropertyValue(BINARY_LIGHT_POWER_STATUS, value);
        return lightState.isOn();
    }

    @Override
    public void turnOn() {
        setPowerStatus(true);
    }

    @Override
    public void turnOff() {
        setPowerStatus(false);
    }

    @Override
    public double getPowerLevel() {

        return (double)light.getLastKnownLightState().getBrightness();
    }

    @Override
    public double setPowerLevel(double level) {
        PHLightState lightState = new PHLightState();
        lightState.setBrightness((int)level);
        bridge.updateLightState(light, lightState);
        super.setPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL, (int)level);
        return level;
    }

    @Override
    public double getMaxPowerLevel() {
        Double maxLevel = (Double) getPropertyValue(BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL);
        if (maxLevel == null)
            return 0;

        return maxLevel;
    }

    @Override
    public void setColor(Color color) {
        PHLightState lightState = new PHLightState();
        float[] xy = PHUtilities.calculateXYFromRGB(color.getRed(), color.getGreen(), color.getBlue(), light.getIdentifier());
        lightState.setX(xy[0]);
        lightState.setY(xy[1]);
        bridge.updateLightState(light, lightState);
    }

    @Override
    public Color getColor() {
        Color color=new Color(0,0,0);
        return color;
    }

}
