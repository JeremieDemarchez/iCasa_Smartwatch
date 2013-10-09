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
package fr.liglab.adele.icasa.distribution.test.zone;

import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;

/**
 * 
 * Used to listen Zone events and track the event information received.
 */
public class ZoneTestListener implements ZoneListener {

	private Zone listenZone = null;
	
	private String listenVariable = null;

	private Object listenOldValue = null;

	private Position listenOldPosition = null;
	
	private Position listenNewPosition = null;

	private Zone listenParentZone = null;
	
	private Zone listenNewParentZone = null;
	
	
	/**
	 * @return the listenZone
	 */
	public Zone getListenZone() {
		return listenZone;
	}
	
	/**
	 * @return the listenVariable
	 */
	public String getListenVariable() {
		return listenVariable;
	}
	
	/**
	 * @return the listenOldValue
	 */
	public Object getListenOldValue() {
		return listenOldValue;
	}
	
	/**
	 * @return the listenOldPosition
	 */
	public Position getListenOldPosition() {
		return listenOldPosition;
	}
	
	/**
	 * @return the listenParentZone
	 */
	public Zone getListenParentZone() {
		return listenParentZone;
	}
	
	
	/**
	 * @return the listenNewPosition
	 */
	public Position getListenNewPosition() {
		return listenNewPosition;
	}

	/**
	 * @return the listenNewParentZone
	 */
	public Zone getListenNewParentZone() {
		return listenNewParentZone;
	}

	
	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZonePropListener#zoneVariableAdded(fr.liglab.adele.icasa.location.Zone, java.lang.String)
	 */
	public void zoneVariableAdded(Zone zone, String variableName) {
		listenZone = zone;
		listenVariable = variableName;
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZonePropListener#zoneVariableRemoved(fr.liglab.adele.icasa.location.Zone, java.lang.String)
	 */
	public void zoneVariableRemoved(Zone zone, String variableName) {
		listenZone = zone;
		listenVariable = variableName;
		
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZonePropListener#zoneVariableModified(fr.liglab.adele.icasa.location.Zone, java.lang.String, java.lang.Object)
	 */
	public void zoneVariableModified(Zone zone, String variableName,
			Object oldValue, Object newValue) {
		listenZone = zone;
		listenVariable = variableName;
		listenOldValue = oldValue;
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZoneListener#zoneAdded(fr.liglab.adele.icasa.location.Zone)
	 */
	public void zoneAdded(Zone zone) {
		listenZone = zone;
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZoneListener#zoneRemoved(fr.liglab.adele.icasa.location.Zone)
	 */
	public void zoneRemoved(Zone zone) {
		listenZone = zone;
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZoneListener#zoneMoved(fr.liglab.adele.icasa.location.Zone, fr.liglab.adele.icasa.location.Position)
	 */
	public void zoneMoved(Zone zone, Position oldPosition, Position newPosition) {
		listenZone = zone;
		listenOldPosition = oldPosition;
		listenNewPosition = newPosition;
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZoneListener#zoneResized(fr.liglab.adele.icasa.location.Zone)
	 */
	public void zoneResized(Zone zone) {
		listenZone = zone;
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.location.ZoneListener#zoneParentModified(fr.liglab.adele.icasa.location.Zone, fr.liglab.adele.icasa.location.Zone)
	 */
	public void zoneParentModified(Zone zone, Zone oldParentZone, Zone newParentZone) {
		listenZone = zone;
		listenParentZone = oldParentZone;
		listenNewParentZone = newParentZone;
	}

    /**
     * Invoked when a device has been attached a zone
     *
     * @param container
     * @param child
     */
    public void deviceAttached(Zone container, LocatedDevice child) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * * Invoked when a device has been detached from a zone
     *
     * @param container
     * @param child
     */
    public void deviceDetached(Zone container, LocatedDevice child) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
