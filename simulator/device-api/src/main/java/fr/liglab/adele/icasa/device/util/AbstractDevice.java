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
package fr.liglab.adele.icasa.device.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Abstract implementation of the {@link GenericDevice} interface that manages
 * the listeners addition, removal and notifications.
 * 
 * @author bourretp
 */
public abstract class AbstractDevice implements GenericDevice {
	
	private String state;
	
	abstract public String getSerialNumber();

	public String getState() {
	   return state;
   }

	public void setState(String state) {
	   this.state = state;	   
   }

    /**
     * The listeners of the device.
     */
    private final List<DeviceListener> m_listeners = new LinkedList<DeviceListener>();

    @Override
    public void addListener(DeviceListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (m_listeners) {
            if (!m_listeners.contains(listener)) {
                m_listeners.add(listener);
            }
        }
    }

    @Override
    public void removeListener(DeviceListener listener) {
        synchronized (m_listeners) {
            m_listeners.remove(listener);
        }
    }

    /**
     * Notify all listeners. In case of exceptions, exceptions are dumped to the
     * standard error stream.
     */
    protected void notifyListeners(/* TODO */) {
        List<DeviceListener> listeners;
        // Make a snapshot of the listeners list
        synchronized (m_listeners) {
            listeners = Collections
                    .unmodifiableList(new ArrayList<DeviceListener>(m_listeners));
        }
        // Call all listeners sequentially
        for (DeviceListener listener : listeners) {
            try {
                listener.notifyDeviceEvent(getSerialNumber());
            } catch (Exception e) {

                Exception ee = new Exception("Exception in device listener '"
                        + listener + "'", e);
                ee.printStackTrace();
            }
        }
    }

}
