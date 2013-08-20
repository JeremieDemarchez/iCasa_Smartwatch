/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.dependency.manager;

import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.icasa.device.util.LocatedDeviceTrackerCustomizer;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;

public class LocatedDeviceManager implements LocatedDeviceTrackerCustomizer {

    private List<LocatedDevice> m_devices = new ArrayList<LocatedDevice>();

    private DeviceDependency m_dependency;
    
    public LocatedDeviceManager(DeviceDependency dependency) {
        m_dependency = dependency;
    }
    
    @Override
    public boolean addingDevice(LocatedDevice device) {
        System.out.println("Adding device " + device.getSerialNumber());
        return true;
    }

    @Override
    public void addedDevice(LocatedDevice device) {
        synchronized (m_devices) {
            m_devices.add(device);
        }
        System.out.println("Added device " + device.getSerialNumber());
        m_dependency.invalidateMatchingServices();
    }

    @Override
    public void removedDevice(LocatedDevice device) {
        synchronized (m_devices) {
            m_devices.remove(device);
        }
        m_dependency.invalidateMatchingServices();        
    }

    @Override
    public void modifiedDevice(LocatedDevice device, String propertyName, Object oldValue, Object newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void movedDevice(LocatedDevice device, Position oldPosition, Position newPosition) {
        // TODO Auto-generated method stub

    }

    public boolean contains(String deviceId) {
        List<LocatedDevice> copyList = new ArrayList<LocatedDevice>();
        synchronized (m_devices) {
            copyList.addAll(m_devices);
        }
        for (LocatedDevice locatedDevice : copyList) {
            if (locatedDevice.getSerialNumber().equals(deviceId)) {
                return true;
            }
        }
        return false;
    }

}
