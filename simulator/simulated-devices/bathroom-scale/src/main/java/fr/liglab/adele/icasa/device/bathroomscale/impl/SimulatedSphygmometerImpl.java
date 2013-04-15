/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.bathroomscale.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.bathroomscale.Sphygmometer;
import fr.liglab.adele.icasa.device.bathroomscale.rest.api.SphygmometerRestAPI;
import fr.liglab.adele.icasa.simulator.SimulationManager;

@Component(name = "iCASA.Sphygmometer")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedSphygmometerImpl extends MedicalDeviceImpl implements Sphygmometer {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@Requires
	private SimulationManager manager;
	
	@Requires(optional = true)
	private SphygmometerRestAPI restAPI;


	public SimulatedSphygmometerImpl() {
		super();
        super.setPropertyValue(MedicalDeviceImpl.LOCATION_PROPERTY_NAME, MedicalDeviceImpl.LOCATION_UNKNOWN);
        super.setPropertyValue(SPHYGMOMETER_CURRENT_SYSTOLIC, 0);
        super.setPropertyValue(SPHYGMOMETER_CURRENT_DIASTOLIC, 0);
        super.setPropertyValue(SPHYGMOMETER_CURRENT_PULSATIONS, 0);
	}

	@Validate
	protected void start() {
		manager.addListener(this);
	}

	@Invalidate
	protected void stop() {
		manager.removeListener(this);
	}

	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
   protected SimulationManager getManager() {
	   return manager;
   }

	@Override
   protected void updateSpecificState() {
		int systolic =  getRandomIntValue(110, 150);
		int diastolic = getRandomIntValue(60, 90);
		int pulsations = getRandomIntValue(60, 100);
		
		setPropertyValue(SPHYGMOMETER_CURRENT_SYSTOLIC, systolic);
		setPropertyValue(SPHYGMOMETER_CURRENT_DIASTOLIC, diastolic);
		setPropertyValue(SPHYGMOMETER_CURRENT_PULSATIONS, pulsations);
		
		if (restAPI != null) {
			try {
				restAPI.sendMeasure(systolic, diastolic, pulsations);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
   }

	@Override
   protected void resetSpecificState() {
		setPropertyValue(SPHYGMOMETER_CURRENT_SYSTOLIC, 0);
		setPropertyValue(SPHYGMOMETER_CURRENT_DIASTOLIC, 0);
		setPropertyValue(SPHYGMOMETER_CURRENT_PULSATIONS, 0);
   }

	@Override
   public int getSystolic() {
		Integer value = (Integer) getPropertyValue(SPHYGMOMETER_CURRENT_SYSTOLIC);
		if (value != null)
			return value;
	   return 0;
   }

	@Override
   public int getDiastolic() {
		Integer value = (Integer) getPropertyValue(SPHYGMOMETER_CURRENT_DIASTOLIC);
		if (value != null)
			return value;
	   return 0;
   }

	@Override
   public int getPulsations() {
		Integer value = (Integer) getPropertyValue(SPHYGMOMETER_CURRENT_PULSATIONS);
		if (value != null)
			return value;
	   return 0;
   }
}
