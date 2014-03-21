package fr.liglab.adele.icasa.device.light.impl;

import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.ColorLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

import java.awt.*;

/**
 * Implementation of a simulated dimmer light device.
 *
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "iCasa.DimmerLight")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedColorLightImpl extends AbstractDevice implements ColorLight, SimulatedDevice {

    @ServiceProperty(name = DimmerLight.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;


    public SimulatedColorLightImpl() {
        super();
        super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(ColorLight.DIMMER_LIGHT_MAX_POWER_LEVEL, 100.0d);
        super.setPropertyValue(ColorLight.DIMMER_LIGHT_POWER_LEVEL, 0.0d);
        super.setPropertyValue(ColorLight.BINARY_LIGHT_POWER_STATUS, false);
        super.setPropertyValue(ColorLight.COLOR_LIGHT_COLOR_LEVEL, new Color(0,0,0));;
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Override
    public synchronized double getPowerLevel() {
        Double powerLevel = (Double) getPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL);
        if (powerLevel == null)
            return 0.0d;
        return powerLevel;
    }

    @Override
    public synchronized double setPowerLevel(double level) {
        if (level < 0.0d || level > 1.0d || Double.isNaN(level))
            throw new IllegalArgumentException("Invalid power level : " + level);
        // Add by jeremy
        setPropertyValue(DimmerLight.DIMMER_LIGHT_POWER_LEVEL, level);

        return level;
    }

    @Override
    public double getMaxPowerLevel() {
        Double maxLevel = (Double) getPropertyValue(DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL);
        if (maxLevel == null)
            return 0;
        return maxLevel;
    }


    @Override
    public boolean getPowerStatus() {
        Boolean powerStatus = (Boolean) getPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS);
        if (powerStatus == null)
            return false;

        return powerStatus;
    }

    @Override
    public boolean setPowerStatus(boolean status) {
        setPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS, (Boolean) status);
        return status;
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
    public synchronized Color setColor(Color color) {
        setPropertyValue(ColorLight.COLOR_LIGHT_COLOR_LEVEL, (Color) color);
        return color;

    }

    @Override
    public synchronized Color getColor() {
        Color color = (Color) getPropertyValue(ColorLight.COLOR_LIGHT_COLOR_LEVEL);
        if (color == null)
            return new Color (0,0,0);

        return color;
    }
}
