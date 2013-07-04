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
package fr.liglab.adele.icasa.device.manager.impl;

import fr.liglab.adele.icasa.device.manager.DependAccessRights;
import fr.liglab.adele.icasa.device.manager.DeviceAccessRight;
import fr.liglab.adele.icasa.device.manager.DeviceDependency;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 */
public class DependAccessRightsImpl implements DependAccessRights {

    private final DeviceDependency _deviceDep;

    private final Set<DeviceAccessRight> _devAccessRights = new HashSet<DeviceAccessRight>();

    public DependAccessRightsImpl(DeviceDependency deviceDep) {
        _deviceDep = deviceDep;
    }

    public void addAccessRight(DeviceAccessRight deviceAR) {
       _devAccessRights.add(deviceAR);
    }

    @Override
    public DeviceDependency getDependency() {
        return _deviceDep;
    }

    @Override
    public Set<DeviceAccessRight> getAccessRights() {
        return _devAccessRights;
    }

    @Override
    public boolean isAllowed(Method method) {
        //TODO

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isExclusive(Method method) {
        //TODO

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
