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
package fr.liglab.adele.icasa.device.security.impl;


import fr.liglab.adele.icasa.device.security.FloodSensor;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

/**
 * Implementation of a CO2 sensor device.
 *
 */
@Component(name = "iCasa.FloodSensor")
@Provides(properties = {@StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION)})
public class SimulatedFloodSensorImpl extends AbstractDevice implements FloodSensor, SimulatedDevice {

    @ServiceProperty(name = FloodSensor.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;


    public SimulatedFloodSensorImpl() {
        super();
        super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(FloodSensor.FLOOD_SENSOR_ALARM, false);
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Override
    public boolean getAlarmStatus() {
        return (boolean)super.getPropertyValue(FloodSensor.FLOOD_SENSOR_ALARM);
    }
}
