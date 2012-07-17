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
package org.medical.device.manager.util;

import org.medical.common.VariableType;
import org.medical.common.impl.StateVariableImpl;

/**
 * This class allows externals to force sending value change notifications.
 * 
 * @author Thomas Leveque
 *
 */
public class ManagedStateVariableImpl extends StateVariableImpl {

	public ManagedStateVariableImpl(String name, Object value, Class type,
			VariableType varType, String description, boolean canBeModified,
			boolean canSendNotif, Object owner) {
		super(name, value, type, varType, description, canBeModified, canSendNotif,
				owner);
	}

	public void sendValueChangeNotifs(Object oldValue) {
		notifyValueChange(oldValue);
	}

	
}
