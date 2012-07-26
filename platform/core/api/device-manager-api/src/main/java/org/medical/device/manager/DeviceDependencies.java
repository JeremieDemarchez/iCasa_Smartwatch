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
package org.medical.device.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows to define a device dependency in a declarative way.
 * Dependencies are interpreted using registration order.
 * 
 * @author Thomas Leveque
 *
 */
public class DeviceDependencies {

	protected final List<DeviceDependency> _dependencies = new ArrayList<DeviceDependency>();
	
	protected List<Class> _proxyInterfaces = new ArrayList<Class>();
	
	protected boolean _createsOnDemand = false;
	
	protected boolean _exclusiveAccess = false;
	
	protected boolean _unavailableDevs = false;
	
	protected boolean _isAllRequired = false;
	
	protected boolean _optional = false;

	public DeviceDependencies() {
		// do nothing
	}
	
	/**
	 * Returns list of interfaces implemented by created proxies.
	 * Returns an empty list if there is no exported interface.
	 * 
	 * @return list of interfaces implemented by created proxies.
	 */
	public final List<Class> getExportedInterfaces() {
		return Collections.unmodifiableList(_proxyInterfaces);
	}
	
	/**
	 * Returns all device dependencies.
	 * 
	 * @return all device dependencies.
	 */
	public final List<DeviceDependency> getDependencies() {
		return Collections.unmodifiableList(_dependencies);
	}
	
	/**
	 * Specifies devices to include to this dependency specification.
	 * 
	 * @return the include device dependency
	 */
	public DeviceDependency includes() {
		DeviceDependency dependency = new DeviceDependency(this, true);
		synchronized (_dependencies) {
			_dependencies.add(dependency);
		}
		
		return dependency;
	}
	
	/**
	 * Specifies devices to exclude from this dependency specification.
	 * 
	 * @return the exclude device dependency
	 */
	public DeviceDependency excludes() {
		DeviceDependency dependency = new DeviceDependency(this, false);
		synchronized (_dependencies) {
			_dependencies.add(dependency);
		}
		
		return dependency;
	}
	
	/**
	 * Specifies that application will get exclusive access to all devices which match this dependency.
	 */
	public DeviceDependencies requiresExclusiveAccess() {
		_exclusiveAccess = true;
		return this;
	}
	
	/**
	 * Defines that proxies of matching devices will be created only when the application will access to them.
	 * @return 
	 */
	public DeviceDependencies createsOnDemand() {
		_createsOnDemand = true;
		return this;
	}
	
	/**
	 * Returns true if exclusive access will be given for corresponding devices.
	 * 
	 * @return true if exclusive access will be given for corresponding devices.
	 */
	public boolean isRequiresExclusiveAccess() {
		return _exclusiveAccess;
	}
	
	/**
	 * Returns true if device proxies are only created on demand.
	 * If set to false, first attempt to get the corresponding proxy may fail due to  technical details of OSGi platform.
	 * 
	 * @return true if device proxies are only created on demand.
	 */
	public boolean isCreatedOnDemand() {
		return _createsOnDemand;
	}
	
	/**
	 * Returns true if all devices are required.
	 * 
	 * @return true if all devices are required.
	 */
	public final boolean isAll() {
		DeviceDependency deviceDep;
		synchronized (_dependencies) {
			//TODO iterate over all deps
			if (_dependencies.isEmpty() || (_dependencies.size() > 1))
				return false;
		
			deviceDep = _dependencies.get(0);
		}
		
		return deviceDep.isIncludes() && deviceDep.isAll();
	}
	
	/**
	 * Defines that all matching devices will be exported as proxies which implements specified interfaces.
	 * There could be a different generated proxy for each specfied interface.
	 * 
	 * @param proxyInterfaces implemented interfaces by created proxies
	 * @return 
	 * @throws IllegalArgumentException if there is no component able to export to specified interface.
	 */
	public DeviceDependencies exportsTo(Class... proxyInterfaces) {
		synchronized (_proxyInterfaces) {
			for (Class proxyInterface : proxyInterfaces)
				_proxyInterfaces.add(proxyInterface);
		}
		return this;
	}
	
	/**
	 * Returns serialized form of the device dependency specification.
	 * 
	 * @return serialized form of the device dependency specification.
	 */
	public String toString() {
		return null; //TODO
	}

	/**
	 * Returns true if specified device matches this device dependencies.
	 * 
	 * @param dev a device
	 * @return true if specified device matches this device dependencies.
	 */
	public boolean matches(Device dev) {
		boolean matches = false;
		synchronized (_dependencies) {
			
			if (!dev.isAvailable() && !isRequireUnavailableDevices())
				return false;
			
			// check includes matching
			for (DeviceDependency dep : _dependencies) {
				if (!dep.isIncludes())
					continue;
				
				if (dep.matches(dev)) {
					matches = true;
					break;
				}
			}
			if (!matches)
				return false;
			
			// check excludes matching
			for (DeviceDependency dep : _dependencies) {
				if (!dep.isExcludes())
					continue;
				
				if (dep.matches(dev))
					return false;
			}
		}
		
		return matches;
	}

	/**
	 * Returns true if we requires also known and unavailable devices.
	 */
	public boolean isRequireUnavailableDevices() {
		return _unavailableDevs;
	}

	/**
	 * Defines that we requires also known and unavailable devices.
	 * 
	 * @return this dependency
	 */
	public DeviceDependencies requiresUnavailableDevices() {
		_unavailableDevs = true;
		return this;
	}
	
	/**
	 * This request ask to return all devices matching it.
	 * 
	 * @return this request.
	 */
	public DeviceDependencies requiresAll() {
		_isAllRequired = true;
		
		return this;
	}
	
	/**
	 * This request ask to return at most one device matching it.
	 * 
	 * @return this request.
	 */
	public DeviceDependencies requiresOne() {
		_isAllRequired = false;

		return this;
	}
	
	/**
	 * Returns true if we require to get all devices matching it.
	 * 
	 * @return true if we require to get all devices matching it.
	 */
	public boolean isRequiresAll() {
		return _isAllRequired;
	}
	
	/**
	 * Returns true if this request ask to return at most one device matching it.
	 * 
	 * @return true if this request ask to return at most one device matching it.
	 */
	public boolean isRequiresOne() {
		return !_isAllRequired;
	}
	
	/**
	 * Defines that this dependency is mandatory.
	 * 
	 * @return this dependency
	 */
	public DeviceDependencies mandatory() {
		_optional = false;
		return this;
	}
	
	/**
	 * Defines that this dependency is optional.
	 * 
	 * @return this dependency
	 */
	public DeviceDependencies optional() {
		_optional = true;
		return this;
	}
	
	/**
	 * Returns true if this dependency is optional.
	 * 
	 * @return true if this dependency is optional.
	 */
	public boolean isOptional() {
		return _optional;
	}
	
	public DeviceDependencies cloneDeps() {
		DeviceDependencies clonedDeps = new DeviceDependencies();
		cloneDepsInternal(clonedDeps);
		
		return clonedDeps;
	}

	protected final void cloneDepsInternal(DeviceDependencies clonedDeps) {
		clonedDeps._createsOnDemand = _createsOnDemand;
		clonedDeps._exclusiveAccess = _exclusiveAccess;
		clonedDeps._optional = _optional;
		clonedDeps._unavailableDevs = _unavailableDevs;
		clonedDeps._isAllRequired = _isAllRequired;
		clonedDeps._proxyInterfaces = (List<Class>) ((ArrayList<Class>) _proxyInterfaces).clone(); // ok because does shallow copy
		synchronized (_dependencies) {
			for (DeviceDependency dep : _dependencies)
				clonedDeps._dependencies.add(dep.cloneDep(clonedDeps));
		}
	}
}
