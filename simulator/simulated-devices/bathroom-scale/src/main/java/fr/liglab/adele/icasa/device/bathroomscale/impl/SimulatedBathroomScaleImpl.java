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
import fr.liglab.adele.icasa.device.bathroomscale.BathroomScale;
import fr.liglab.adele.icasa.device.bathroomscale.rest.api.BathroomScaleRestAPI;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulationManager;

@Component(name = "iCASA.BathroomScale")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedBathroomScaleImpl extends MedicalDeviceImpl implements BathroomScale {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@Requires
	private SimulationManager manager;

	@Requires(optional = true)
	private BathroomScaleRestAPI restAPI;

	Zone detectionZone = null;

	public SimulatedBathroomScaleImpl() {
		super();
		setPropertyValue(WEIGHT_PROPERTY, 0.0);
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
	public float getCurrentWeight() {
		Float weight = (Float) getPropertyValue(WEIGHT_PROPERTY);
		if (weight != null)
			return weight;
		return 0.0f;
	}

	@Override
   protected SimulationManager getManager() {
	   return manager;
   }

	@Override
   protected void updateSpecificState() {
		float weight = getRandomFloatValue(55, 95);
		setPropertyValue(WEIGHT_PROPERTY, weight);
		if (restAPI != null) {
			try {
				restAPI.sendMeasure(weight);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	   
   }

	@Override
   protected void resetSpecificState() {
		setPropertyValue(WEIGHT_PROPERTY, 0.0f);	   
   }
	
	

}
