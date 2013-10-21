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
package fr.liglab.adele.icasa.location.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.context.impl.ContextManagerImpl;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatedDeviceImpl extends LocatedObjectImpl implements LocatedDevice, DeviceListener<GenericDevice> {

	/**
	 * Device Serial Number
	 */
	private String m_serialNumber;

	/**
	 * Device Instance (of iPOJO Device Component)
	 */
	private GenericDevice deviceComponent;

	/**
	 * Device type (iPOJO Device Factory)
	 */
	private String _type;
	
	
	private ContextManager manager;
	
	private final List<LocatedDeviceListener> listeners = new ArrayList<LocatedDeviceListener>();

	private ReadWriteLock lock = new ReentrantReadWriteLock();

	public LocatedDeviceImpl(String serialNumber, Position position, GenericDevice deviceComponent, String type,
	      ContextManager manager) {
		super(position);
		this.m_serialNumber = serialNumber;
		this.deviceComponent = deviceComponent;
		this.manager = manager;
		this._type = type;
	}

	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	@Override
	public String getType() {
		return _type;
	}

	@Override
	public Set<String> getProperties() {
		return deviceComponent.getProperties();
	}

	@Override
	public Object getPropertyValue(String propertyName) {
		if (deviceComponent != null)
			return deviceComponent.getPropertyValue(propertyName);
		return null;
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) {

		if (deviceComponent == null){
		    logger.warn("Unable to set property in Located Device. deviceComponent is null");
			return;
        }
        logger.debug(" ["+getSerialNumber()+"] Set property in Located Device");
		// TODO: Test which properties are readonly
		deviceComponent.setPropertyValue(propertyName, value);
	}
	
	@Override
   public boolean constainsProperty(String propertyName) {
		if (deviceComponent != null)
			return deviceComponent.constainsProperty(propertyName);
	   return false;
   }

	@Override
   public boolean hasPropertyValue(String propertyName) {
		if (deviceComponent != null)
			return deviceComponent.hasPropertyValue(propertyName);
	   return false;
   }

	@Override
   public boolean removeProperty(String propertyName) {
		if (deviceComponent != null){
            logger.debug(" ["+getSerialNumber()+"] Remove property in Located Device");
			return deviceComponent.removeProperty(propertyName);
        }
        logger.warn(" ["+getSerialNumber()+"] Unable to remove property. DeviceComponent is null");
	   return false;
   }
	

	@Override
	public void addListener(final LocatedDeviceListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		lock.writeLock().lock();
		try {
			listeners.add(listener);
		} finally {
			lock.writeLock().unlock();
		}

	}

	@Override
	public void removeListener(final LocatedDeviceListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		lock.writeLock().lock();
		try {
			listeners.remove(listener);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void setCenterAbsolutePosition(Position position) {
		Position oldPosition = getCenterAbsolutePosition();
		super.setCenterAbsolutePosition(position);
		List<LocatedDeviceListener> snapshotListener = getListenerCopy();
		// Listeners notification
		for (LocatedDeviceListener listener : snapshotListener) {
			try {
				listener.deviceMoved(this, oldPosition, position.clone());
			} catch (Exception ex) {
				logger.error("Listener in deviceMoved event has throw an exception: " + listener);
				ex.printStackTrace();
			}
		}
        updatePosition();
	}

    public void updatePosition(){
        // Computes the new location
        if (deviceComponent != null) {
            Zone zone = manager.getZoneFromPosition(getCenterAbsolutePosition());
            String location = GenericDevice.LOCATION_UNKNOWN;
            if (zone != null)
                location = zone.getId();
            deviceComponent.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, location);
        }
    }

	@Override
	protected void notifyAttachedObject(LocatedObject attachedObject) {
		LocatedDevice childDevice = null;
		if (attachedObject instanceof LocatedDevice) {
			childDevice = (LocatedDevice) attachedObject;
		} else {
			return; // If attached object is not a locatedDevice we do nothing.
		}
		List<LocatedDeviceListener> snapshotListener = getListenerCopy();
		for (LocatedDeviceListener listener : snapshotListener) {
			try {
				listener.deviceAttached(this, childDevice);
			} catch (Exception ex) {
				logger.error("Listener has throw an exception: " + listener);
				ex.printStackTrace();
			}
		}
	}

	@Override
	protected void notifyDetachedObject(LocatedObject attachedObject) {
		LocatedDevice childDevice = null;
		if (attachedObject instanceof LocatedDevice) {
			childDevice = (LocatedDevice) attachedObject;
		} else {
			return; // If attached object is not a locatedDevice we do nothing.
		}
		List<LocatedDeviceListener> snapshotListener = getListenerCopy();
		for (LocatedDeviceListener listener : snapshotListener) {
			try {
				listener.deviceDetached(this, childDevice);
			} catch (Exception ex) {
				logger.error("Listener has throw an exception: " + listener);
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "Id: " + getSerialNumber() + " - Position: " + getCenterAbsolutePosition();
	}

	@Override
	public void enterInZones(List<Zone> zones) {
		deviceComponent.enterInZones(zones);
        updatePosition();
	}

	@Override
	public void leavingZones(List<Zone> zones) {
		deviceComponent.leavingZones(zones);
        updatePosition();
	}

	@Override
	public GenericDevice getDeviceObject() {
		return deviceComponent;
	}

	// --- Listeners methods -- //

	@Override
	public void deviceAdded(GenericDevice device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceRemoved(GenericDevice device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		List<LocatedDeviceListener> snapshotListener = getListenerCopy();
		for (LocatedDeviceListener listener : snapshotListener) {
			try {
				listener.devicePropertyModified(this, propertyName, oldValue, newValue);
			} catch (Exception ex) {
				logger.error("Listener has throw an exception: " + listener);
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void devicePropertyAdded(GenericDevice device, String propertyName) {
		List<LocatedDeviceListener> snapshotListener = getListenerCopy();
		for (LocatedDeviceListener listener : snapshotListener) {
			try {
				listener.devicePropertyAdded(this, propertyName);
			} catch (Exception ex) {
                logger.error("Listener has throw an exception: " + listener);
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void devicePropertyRemoved(GenericDevice device, String propertyName) {
		List<LocatedDeviceListener> snapshotListener = getListenerCopy();
		for (LocatedDeviceListener listener : snapshotListener) {
			try {
				listener.devicePropertyRemoved(this, propertyName);
			} catch (Exception ex) {
                logger.error("Listener has throw an exception: " + listener);
				ex.printStackTrace();
			}
		}
	}

    /**
     * Callback notifying when the device want to trigger an event.
     *
     * @param device the device triggering the event.
     * @param data   the content of the event.
     */
    @Override
    public void deviceEvent(GenericDevice device, Object data) {
        List<LocatedDeviceListener> snapshotListener = getListenerCopy();
        for (LocatedDeviceListener listener : snapshotListener) {
            try {
                listener.deviceEvent(this, data);
            } catch (Exception ex) {
                logger.error("Listener has throw an exception: " + listener);
                ex.printStackTrace();
            }
        }    }

    @Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		LocatedDeviceImpl that = (LocatedDeviceImpl) o;
		
		if (!String.valueOf(_type).equals(String.valueOf(that._type)))
			return false;
		if (!m_serialNumber.equals(that.m_serialNumber))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = m_serialNumber.hashCode();
		result = 31 * result + _type.hashCode();
		return result;
	}

	/**
	 * Get a copy of the listener to iterate in it.
	 * 
	 * @return a copy of the listeners
	 */
	private List<LocatedDeviceListener> getListenerCopy() {
		lock.readLock().lock();
		try {
			return new ArrayList<LocatedDeviceListener>(listeners);
		} finally {
			lock.readLock().unlock();
		}
	}



}
