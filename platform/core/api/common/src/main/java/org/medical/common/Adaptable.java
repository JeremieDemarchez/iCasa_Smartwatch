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
package org.medical.common;

/**
 * Utility interface to transform an object to an expected format or type.
 * 
 * @author Thomas Leveque
 *
 */
public interface Adaptable {

	/**
	 * Returns an equivalent (as much as possible) representation to this object which is instanceof target class. 
	 * 
	 * @param targetClass expected object class
	 * @return an equivalent (as much as possible) representation to this object which is instanceof target class. 
	 */
	public Object adaptTo(Class<?> targetClass);
}
