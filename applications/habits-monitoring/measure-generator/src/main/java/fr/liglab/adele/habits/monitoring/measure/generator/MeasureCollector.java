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
package fr.liglab.adele.habits.monitoring.measure.generator;

import java.util.HashMap;
import java.util.Map;

import fr.liglab.adele.icasa.clock.api.Clock;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.cilia.Data;
import fr.liglab.adele.cilia.framework.AbstractCollector;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 */
public class MeasureCollector extends AbstractCollector implements DeviceListener {

	private Clock clock;

	private Map<String, PresenceSensor> detectors;

	private int counter = 1;

	private static final Logger logger = LoggerFactory.getLogger(MeasureCollector.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.device.DeviceListener#notifyDeviceEvent()
	 */

	/**
	 * It is called when a new service of type PresenceDetector is registered
	 * into the gateway. (callback method, see metadata.xml).
	 * 
	 * @param detector
	 *           A new PresenceDetector (proxy)
	 */
	public void bindProxy(PresenceSensor detector) {
		logger.info("A new proxy has been found, id " + detector.getSerialNumber());
		if (detectors == null)
			detectors = new HashMap<String, PresenceSensor>();
		detectors.put(detector.getSerialNumber(), detector);
		detector.addListener(this);
	}
	
	public void unbindProxy(PresenceSensor detector) {
		logger.info("A proxy is now outside from the zone, id " + detector.getSerialNumber());
		if (detectors!=null)
			detectors.remove(detector.getSerialNumber());
		detector.removeListener(this);			
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.icasa.device.DeviceListener#notifyDeviceEvent(java.lang
	 * .String)
	 */
	public void notifyDeviceEvent(String deviceSerialNumber) {

		PresenceSensor detector = (PresenceSensor) detectors.get(deviceSerialNumber);

		if (detector != null) {
			if (detector.getSensedPresence()) {
				Measure measure = new Measure();
				measure.setDeviceId(deviceSerialNumber);
				measure.setLocalisation((String) detector.getPropertyValue("Location"));
				long date = clock.currentTimeMillis();
				measure.setTimestamp(date);
				Data data = new Data(measure);

				// simulation realibility
				int res = counter % 4;
				if (res != 0)
					measure.setRealibility(100);
				else
					measure.setRealibility(60);
				counter++;
								
				notifyDataArrival(data);
			}
		}

	}

    public void deviceAdded(GenericDevice device) {
        //do nothing
    }

    public void deviceRemoved(GenericDevice device) {
        //do nothing
    }

    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue) {
        notifyDeviceEvent(device.getSerialNumber());
    }

    public void devicePropertyAdded(GenericDevice device, String propertyName) {
        notifyDeviceEvent(device.getSerialNumber());
    }

    public void devicePropertyRemoved(GenericDevice device, String propertyName) {
        notifyDeviceEvent(device.getSerialNumber());
    }

}
