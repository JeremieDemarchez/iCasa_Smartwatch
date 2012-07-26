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

/**
 * Represents the lifecycle of a variable.
 * Usually, it is synchronized to an entity lifecycle.
 * 
 * @author Thomas Leveque
 *
 */
public enum VariableLifeCycle {
	KNOWN_SYNC /* exists only when the known device exists */, 
	AVAILABLE_SYNC /* exists only when the available device exists */, 
	PROVIDED_SYNC /* exists only when the provider device exists */ , 
	APPLICATION_SYNC /* exists only when the application device exists */ , 
	ALWAYS /* always exists */;

	public static Object getLifeCycle(Device device) {
		if (device instanceof AvailableDevice)
			return AVAILABLE_SYNC;
		if (device instanceof ProvidedDevice)
			return PROVIDED_SYNC;
		if (device instanceof KnownDevice)
			return KNOWN_SYNC;
		if (device instanceof KnownDevice)
			return APPLICATION_SYNC;
		return null;
	}
}
