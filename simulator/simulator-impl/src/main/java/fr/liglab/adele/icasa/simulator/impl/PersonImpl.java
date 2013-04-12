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
package fr.liglab.adele.icasa.simulator.impl;

import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.impl.LocatedObjectImpl;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;

/**
 * TODO
 * 
 * @author Thomas Leveque Date: 10/11/12
 */
public class PersonImpl  extends LocatedObjectImpl implements Person {

	private String m_name;
	
	private String personType;

	private List<PersonListener> listeners = new ArrayList<PersonListener>();
	
	private SimulationManager manager;

	public PersonImpl(String name, Position position, String personType, SimulationManager manager) {
		super(position);
		m_name = name;
		this.personType = personType;
		this.manager = manager;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getLocation() {
		Zone zone = manager.getZoneFromPosition(getCenterAbsolutePosition());
		if (zone!=null)			
			return zone.getId();
		return "unknown";
	}

	@Override
	public void addListener(PersonListener listener) {
        synchronized (listeners){
		    listeners.add(listener);
        }
	}

	@Override
	public void removeListener(PersonListener listener) {
        synchronized (listeners){
		    listeners.remove(listener);
        }
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}


	@Override
	public void setCenterAbsolutePosition(Position position) {
		Position oldPosition = getCenterAbsolutePosition();
		super.setCenterAbsolutePosition(position);

		// Listeners notification
        synchronized (listeners){
            for (PersonListener listener : listeners) {
                listener.personMoved(this, oldPosition);
            }
        }
	}

    @Override
    protected void notifyAttachedObject(LocatedObject attachedObject) {
        LocatedDevice device;

        if (attachedObject instanceof LocatedDevice){
            device = (LocatedDevice)attachedObject;
        }else {
            return; //nothing to notify.
        }
        synchronized (listeners){
            for (PersonListener listener : listeners) {
                listener.personDeviceAttached(this, device);
            }
        }
    }

    @Override
    protected void notifyDetachedObject(LocatedObject attachedObject) {
        LocatedDevice device;

        if (attachedObject instanceof LocatedDevice){
            device = (LocatedDevice)attachedObject;
        }else {
            return; //nothing to notify.
        }
        synchronized (listeners){
            for (PersonListener listener : listeners) {
                listener.personDeviceDetached(this, device);
            }
        }
    }

    @Override
	public String toString() {
		return "Person: " + m_name + " - Position: " + getCenterAbsolutePosition() + " - Type: " + getPersonType(); 
	}

	@Override
   public String getPersonType() {
	   return personType;
   }

	@Override
   public void setPersonType(String personType) {
		this.personType = personType;	   
   }

}
