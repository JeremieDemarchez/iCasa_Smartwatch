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

import java.util.List;
import java.util.Random;

import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;

public abstract class MedicalDeviceImpl extends AbstractDevice implements LocatedDeviceListener, PersonListener, SimulatedDevice {

	protected Zone detectionZone = null;
	
	public final String PRESENCE_DETECTED_PROPERTY = "presence_detected";
	
	public final String DETECTION_SCOPE = "detection_scope"; 

	public MedicalDeviceImpl() {
		setPropertyValue(PRESENCE_DETECTED_PROPERTY, false);
		setPropertyValue(DETECTION_SCOPE, 33);
	}
	
	protected boolean personInZone() {
		for (Person person : getManager().getPersons()) {
			if (detectionZone.contains(person))
				return true;
		}
		return false;
	}
	
	protected float getRandomFloatValue(float min, float max) {
		return min +  new Random().nextFloat() * (max - min);
	}
	
	protected int getRandomIntValue(int min, int max) {
		return min +  new Random().nextInt(max - min);
	}
	
	
	protected abstract SimulationManager getManager();
	
	protected abstract void updateSpecificState();
	protected abstract void resetSpecificState();
	
	private void updateState() {
		if (getState().equals(AbstractDevice.STATE_ACTIVATED) && getFault().equals(AbstractDevice.FAULT_NO)) {

			boolean personFound = personInZone();
			boolean previousDetection = (Boolean) getPropertyValue(PRESENCE_DETECTED_PROPERTY);

			if (!previousDetection) { //New person on Bathroom scale
				if (personFound) {					
					
					setPropertyValue(PRESENCE_DETECTED_PROPERTY, true);
					updateSpecificState();
				}
			} else {
				if (!personFound) { //The person has leave the bathroom scale detection zone
					setPropertyValue(PRESENCE_DETECTED_PROPERTY, false);
					resetSpecificState();
				}
			}
		}
	}
	
	@Override
   public void personAdded(Person person) {
		updateState();
   }

	@Override
   public void personRemoved(Person person) {
		updateState();	   
   }

	@Override
   public void personMoved(Person person, Position oldPosition) {
		updateState();	   
   }

	
	@Override
   public void deviceAdded(LocatedDevice device) {
		if (device.getSerialNumber().equals(getSerialNumber())) {
			String zoneId = getSerialNumber() + "#zone";
			Position center = device.getCenterAbsolutePosition();
			int detectionScope = (Integer) getPropertyValue(DETECTION_SCOPE);
			detectionZone = getManager().createZone(zoneId, center, detectionScope);
			device.attachObject(detectionZone);
		}	   
   }
	
	@Override
   public void deviceRemoved(LocatedDevice device) {
		if (device.getSerialNumber().equals(getSerialNumber())) {
			device.detachObject(detectionZone);
			getManager().removeZone(detectionZone.getId());
		}
	   
   }

	@Override
   public void deviceMoved(LocatedDevice device, Position oldPosition) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void devicePropertyAdded(LocatedDevice device, String propertyName) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
	   // TODO Auto-generated method stub
	   
   }

    @Override
    public void deviceAttached(LocatedDevice locatedDevice, LocatedDevice locatedDevice2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deviceDetached(LocatedDevice locatedDevice, LocatedDevice locatedDevice2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
   public void personDeviceAttached(Person person, LocatedDevice device) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void personDeviceDetached(Person person, LocatedDevice device) {
	   // TODO Auto-generated method stub
	   
   }
	
	public void enterInZones(List<Zone> zones) {
		
	}
	
	public void leavingZones(List<Zone> zones) {
		
	}


}
