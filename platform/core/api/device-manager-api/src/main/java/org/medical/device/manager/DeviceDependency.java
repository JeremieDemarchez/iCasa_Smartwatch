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

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a device dependency specification.
 * 
 * @author Thomas Leveque
 *
 */
public class DeviceDependency {
	
	private final DeviceDependencies _dependencies;
	
	private boolean _isAll = false;
	
	private Set<String> _servSpecs = new HashSet<String>();

	private boolean _includes;
	
	DeviceDependency(DeviceDependencies dependencies, boolean includes) {
		_dependencies = dependencies;
		_includes = includes;
	}

	/**
	 * Defines that we consider all devices.
	 * 
	 * @return this dependency
	 */
	public DeviceDependency all() {
		_isAll  = true;
		
		return this;
	}
	
	/**
	 * Defines that we consider all devices which provides the specified specification.
	 * 
	 * @param spec a interface which represents a service specification
	 * @return this dependency
	 */
	public DeviceDependency withService(String spec) {
		_isAll = false;
		_servSpecs.add(spec);
		
		return this;
	}
	
	public DeviceDependency withService(Class<?> spec) {
		return withService(spec.getName());
	}
	
	/**
	 * Returns serialized form of the device dependency specification.
	 * 
	 * @return serialized form of the device dependency specification.
	 */
	public String toString() {
		if (_isAll)
			return "all";
		if (!_servSpecs.isEmpty()) {
			StringBuffer specsStr = new StringBuffer();
			specsStr.append("specifications(");
			for (String spec : _servSpecs)
				specsStr.append(spec);
			specsStr.append(")");
			return specsStr.toString();
		}
		
		return "undefined"; 
	}

	public boolean isAll() {
		return _isAll;
	}

	/**
	 * Returns true if this dependency represents inclusion of specified devices.
	 * Else represents exclusion of specified devices.
	 * 
	 * @return true if this dependency represents inclusion of specified devices.
	 */
	public boolean isIncludes() {
		return _includes;
	}
	
	/**
	 * Returns true if this dependency represents exclusion of specified devices.
	 * Else represents inclusion of specified devices.
	 * 
	 * @return true if this dependency represents exclusion of specified devices.
	 */
	public boolean isExcludes() {
		return !_includes;
	}

	public boolean matches(Device dev) {
		if (_isAll)
			return true;
		
		for (String spec : _servSpecs) {
			if (!dev.hasServiceType(spec))
				return false;
		}
		
		//TODO perform match with attributes and other props
		
		return false;
	}

	public DeviceDependencies getDependencies() {
		return _dependencies;
	}

	final DeviceDependency cloneDep(DeviceDependencies dependencies) {
		DeviceDependency dep = new DeviceDependency(dependencies, _includes);
		dep._isAll = _isAll;
		dep._servSpecs = (Set<String>) ((HashSet<String>) _servSpecs).clone();
		
		return dep;
	}

}
